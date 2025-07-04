package cn.timflux.storyseek.ai.model.impl;

import cn.timflux.storyseek.ai.model.ModelProvider;
import cn.timflux.storyseek.ai.model.ChatModel;
import cn.timflux.storyseek.ai.model.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: OpenAIChatModel
 * Package: cn.timflux.storyseek.ai.model.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/28 下午6:00
 * @Version 1.0
 */
@Component
@ModelProvider(name = "openai")
public class OpenAIChatModel implements ChatModel {

    @Autowired
    private WebClient openAIWebClient;

    @Override
    public Flux<String> streamChat(List<ChatMessage> messages) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-3.5-turbo");
        request.put("stream", true);
        request.put("messages", messages);

        return openAIWebClient.post()
            .uri("/v1/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(String.class)
            .filter(line -> line.startsWith("data: "))
            .map(line -> line.substring(6))
            .takeUntil(line -> line.equals("[DONE]"));
    }

    @Override
    public String chat(List<ChatMessage> messages) {
        return "";
    }
}