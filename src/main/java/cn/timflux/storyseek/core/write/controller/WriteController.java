package cn.timflux.storyseek.core.write.controller;

import cn.timflux.storyseek.ai.model.ChatModel;
import cn.timflux.storyseek.ai.model.dto.ChatMessage;
import cn.timflux.storyseek.ai.service.ChatModelFactory;
import cn.timflux.storyseek.core.write.dto.PromptRequestDTO;
import cn.timflux.storyseek.core.write.strategy.WritingStageStrategy;
import cn.timflux.storyseek.core.write.strategy.WritingStageStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
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

    /**
     * SSE 方式返回大模型响应结果
     */
    @PostMapping("/stream")
    public SseEmitter stream(@RequestBody PromptRequestDTO request) {
        log.info("收到写作请求，阶段: {}, 模型: {}", request.getStage(), request.getModel());

        WritingStageStrategy strategy = strategyFactory.getStrategy(request.getStage());
        List<ChatMessage> messages = strategy.buildPrompt(request);

        ChatModel chatModel = chatModelFactory.getModel(request.getModel());
        SseEmitter emitter = new SseEmitter();

        chatModel.streamChat(messages).subscribe(
            chunk -> {
                log.debug("发送 SSE 数据块: {}", chunk);
                try {
                    emitter.send(SseEmitter.event().data(chunk));
                } catch (IOException e) {
                    log.error("SSE 发送失败", e);
                    emitter.completeWithError(e);
                }
            },
            error -> {
                log.error("大模型响应异常", error);
                emitter.completeWithError(error);
            },
            () -> {
                log.info("大模型响应完毕");
                emitter.complete();
            }
        );

        return emitter;
    }

}
