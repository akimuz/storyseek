package cn.timflux.storyseek.core.write.edit.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.timflux.storyseek.ai.model.ChatModel;
import cn.timflux.storyseek.ai.model.dto.ChatMessage;
import cn.timflux.storyseek.ai.service.ChatModelFactory;
import cn.timflux.storyseek.core.user.service.UserService;
import cn.timflux.storyseek.core.write.edit.dto.PromptRequestDTO;
import cn.timflux.storyseek.core.write.strategy.WritingStageStrategyFactory;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName: WriteController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:32
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/write")
@RequiredArgsConstructor
public class WriteController {

    private final ChatModelFactory chatModelFactory;
    private final WritingStageStrategyFactory strategyFactory;
    private final UserService userService;
    private final MeterRegistry meterRegistry;

    private final AtomicInteger sseConnectionCounter = new AtomicInteger(0);

    @PostConstruct
    public void initMetrics() {
        Gauge.builder("storyseek_sse_write_connections", sseConnectionCounter, AtomicInteger::get)
            .description("当前活跃 SSE 写作连接数")
            .register(meterRegistry);
    }
    /**
     * SSE 方式返回大模型响应结果
     */
    @PostMapping("/stream")
    public SseEmitter stream(@RequestBody PromptRequestDTO request) {
        long startTime = System.nanoTime();
        AtomicInteger chunkCount = new AtomicInteger(0);
        String stage = request.getStage();
        String model = request.getModel();

        meterRegistry.counter("write_stream_requests_total", "stage", stage, "model", model).increment();

        sseConnectionCounter.incrementAndGet();
        SseEmitter emitter = new SseEmitter(60_000L);
        StringBuilder generatedText = new StringBuilder();

        try {
            List<ChatMessage> messages = new ArrayList<>(strategyFactory
                    .getStrategy(stage)
                    .buildPrompt(request));
            messages.addFirst(ChatMessage.system(
                "请直接生成内容，输出应为纯净的文本。信息已完整，无需补充。\n" +
                "禁止行为：\n- 不要有“好的”、“请查看”、“以下是…”等服务用语\n" +
                "- 不要对信息进行任何解释\n" +
                "- 不要提出问题或要求补充"
            ));

            ChatModel chatModel = chatModelFactory.getModel(model);
            Long userId = StpUtil.getLoginIdAsLong();
            Long inspiration = userService.getUserInspiration(userId);
            int MAX_GENERATE = 3000; // 最大生成字数，同时作为生成功能限制最小字数包

            if (inspiration == null || inspiration < MAX_GENERATE) {
                emitter.send(SseEmitter.event().name("error").data("字数包不足，至少需要 " + MAX_GENERATE + " 字数才能生成内容。本次不扣除字数包。"));
                emitter.complete();
                sseConnectionCounter.decrementAndGet();
                return emitter;
            }

            chatModel.streamChat(messages)
                .publishOn(Schedulers.boundedElastic())
                .subscribe(
                    chunk -> {
                        try {
                            emitter.send(SseEmitter.event().data(chunk));
                            chunkCount.incrementAndGet();
                            generatedText.append(chunk);
                            meterRegistry.counter("write_stream_events_total", "stage", stage, "model", model).increment();
                        } catch (IOException e) {
                            recordError(stage, model, "send_failed");
                            emitter.completeWithError(e);
                        }
                    },
                    error -> {
                        recordError(stage, model, "stream_error");
                        try {
                            emitter.send(SseEmitter.event().name("error").data("生成失败，请稍后重试"));
                        } catch (IOException ex) {
                            recordError(stage, model, "error_send_failed");
                        } finally {
                            emitter.completeWithError(error);
                            meterRegistry.counter("write_stream_ends_total", "stage", stage, "model", model, "end_type", "error").increment();
                            sseConnectionCounter.decrementAndGet();
                        }
                    },
                    () -> {
                        emitter.complete();
                        long durationNs = System.nanoTime() - startTime;
                        long charCount = generatedText.length();

                        Timer.builder("write_stream_latency_seconds")
                            .publishPercentileHistogram()
                            .publishPercentiles(0.5, 0.95, 0.99)
                            .minimumExpectedValue(Duration.ofMillis(1))
                            .maximumExpectedValue(Duration.ofSeconds(30))
                            .tags("stage", stage, "model", model, "status", "success")
                            .register(meterRegistry)
                            .record(durationNs, TimeUnit.NANOSECONDS);

                        meterRegistry.counter("storyseek_sse_write_chunks_total", "stage", stage, "model", model)
                            .increment(chunkCount.get());

                        meterRegistry.counter("storyseek_sse_write_chars_total", "stage", stage, "model", model)
                            .increment(charCount);

                        try {
                            userService.consumeInspiration(userId, charCount, stage, request.toString());
                        } catch (Exception e) {
                            recordError(stage, model, "consume_failed");
                            log.error("灵感值扣除失败", e);
                        }

                        meterRegistry.counter("write_stream_ends_total", "stage", stage, "model", model, "end_type", "completed").increment();
                        sseConnectionCounter.decrementAndGet();
                    }
                );

            emitter.onTimeout(() -> {
                recordError(stage, model, "timeout");
                meterRegistry.counter("write_stream_ends_total", "stage", stage, "model", model, "end_type", "timeout").increment();
                emitter.complete();
                sseConnectionCounter.decrementAndGet();
            });

            emitter.onError((e) -> {
                recordError(stage, model, "connection_error");
                meterRegistry.counter("write_stream_ends_total", "stage", stage, "model", model, "end_type", "error").increment();
                sseConnectionCounter.decrementAndGet();
            });

            emitter.onCompletion(() -> {
                meterRegistry.counter("write_stream_ends_total", "stage", stage, "model", model, "end_type", "completed").increment();
                sseConnectionCounter.decrementAndGet();
            });

        } catch (Exception e) {
            log.error("写作流式接口异常", e);
            try {
                emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
            } catch (IOException ex) {
                recordError(stage, model, "controller_exception_send_failed");
            }
            emitter.completeWithError(e);
            recordError(stage, model, "controller_exception");
            meterRegistry.counter("write_stream_ends_total", "stage", stage, "model", model, "end_type", "controller_exception").increment();
            sseConnectionCounter.decrementAndGet();
        }

        return emitter;
    }

    private void recordError(String stage, String model, String errorType) {
        meterRegistry.counter("storyseek_sse_write_errors_total",
            "stage", stage,
            "model", model,
            "error_type", errorType
        ).increment();
    }
}
