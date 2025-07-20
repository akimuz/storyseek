package cn.timflux.storyseek.core.write.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.timflux.storyseek.ai.model.ChatModel;
import cn.timflux.storyseek.ai.model.dto.ChatMessage;
import cn.timflux.storyseek.ai.service.ChatModelFactory;
import cn.timflux.storyseek.core.user.service.UserService;
import cn.timflux.storyseek.core.write.dto.PromptRequestDTO;
import cn.timflux.storyseek.core.write.strategy.WritingStageStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: WriteController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:32
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/api/write")
public class WriteController {

    @Autowired
    private ChatModelFactory chatModelFactory;
    @Autowired private WritingStageStrategyFactory strategyFactory;
    @Autowired private UserService userService;

    /**
     * SSE 方式返回大模型响应结果
     */
    @PostMapping("/stream")
    public SseEmitter stream(@RequestBody PromptRequestDTO request) {
        log.info("收到写作请求，阶段: {}, 模型: {}", request.getStage(), request.getModel());
        SseEmitter emitter = new SseEmitter();
        try {
            List<ChatMessage> messages = new ArrayList<>(strategyFactory
                .getStrategy(request.getStage())
                .buildPrompt(request)
            );
            messages.addFirst(ChatMessage.system(
                "请直接生成内容，输出应为纯净的文本。信息已完整，无需补充。" +
                "\n" +
                "禁止行为：\n" +
                "\n" +
                "- 不要有“好的”、“请查看”、“以下是…”等服务用语\n" +
                "\n" +
                "- 不要对信息进行任何解释\n" +
                "\n" +
                "- 不要提出问题或要求补充"
            ));
            log.debug("拼接后的完整提示词：{}", messages);
            ChatModel chatModel = chatModelFactory.getModel(request.getModel());
            StringBuilder generatedText = new StringBuilder();
            Long userId = StpUtil.getLoginIdAsLong();
            Long inspiration = userService.getUserInspiration(userId);
            int MAX_GENERATE = 3000; // 最大生成字数
            if (inspiration == null || inspiration < MAX_GENERATE) {
                // 直接通过SSE返回错误信息
                emitter.send(SseEmitter.event().name("error").data("字数包不足，至少需要" + MAX_GENERATE + "字数才能生成内容。本次不扣除字数包。"));
                emitter.complete();
                return emitter;
            }
            chatModel.streamChat(messages).subscribe(
                chunk -> {
                    log.debug("发送 SSE 数据块: {}", chunk);
                    generatedText.append(chunk);
                    try {
                        emitter.send(SseEmitter.event().data(chunk));
                        emitter.send(SseEmitter.event().comment("keep-alive"));
                    } catch (IOException e) {
                        log.error("SSE 发送失败", e);
                        emitter.completeWithError(e);
                    }
                },
                error -> {
                    log.error("大模型响应异常", error);
                    try {
                        emitter.send(SseEmitter.event().name("error").data("生成失败，请稍后重试"));
                    } catch (IOException ex) {
                        log.error("SSE 发送错误信息失败", ex);
                    }
                    emitter.completeWithError(error);
                },
                () -> {
                    log.info("大模型响应完毕");
                    emitter.complete();
                    // 生成完毕后统一扣除灵感值
                    try {
                        long wordCount = generatedText.length();
                        userService.consumeInspiration(
                            userId,
                            wordCount,
                            request.getStage(),
                            request.toString()
                        );
                    } catch (Exception e) {
                        log.error("灵感值扣除失败", e);
                    }
                }
            );
        } catch (Exception e) {
            log.error("写作流式接口异常", e);
            try {
                emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
            } catch (IOException ex) {
                log.error("SSE 发送错误信息失败", ex);
            }
            emitter.completeWithError(e);
        }
        return emitter;
    }
}
