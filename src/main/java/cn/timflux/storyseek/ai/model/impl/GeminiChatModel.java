package cn.timflux.storyseek.ai.model.impl;

import cn.timflux.storyseek.ai.model.ChatModel;
import cn.timflux.storyseek.ai.model.ModelProvider;
import cn.timflux.storyseek.ai.model.dto.ChatMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * ClassName: GeminiChatModel
 * Package: cn.timflux.storyseek.ai.model.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/28 下午6:00
 * @Version 1.0
 */

@Slf4j
@Component
@ModelProvider(name = "gemini")
public class GeminiChatModel implements ChatModel {

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${google.api.url}")
    private String apiUrl;

    private final WebClient geminiWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiChatModel(WebClient webClient) {
        this.geminiWebClient = webClient;
    }

    @Override
    public Flux<String> streamChat(List<ChatMessage> messages) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "gemini-2.0-flash");
        request.put("stream", true);
        request.put("messages", messages);

        return geminiWebClient.post()
            .uri(apiUrl)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(String.class)
            .takeUntil(line -> line.equals("[DONE]"))
            .flatMap(data -> {
                if ("[DONE]".equals(data)) {
                    return Flux.empty();
                }
                try {
                    JsonNode node = objectMapper.readTree(data);
                    JsonNode contentNode = node
                        .path("choices").get(0)
                        .path("delta")
                        .path("content");
                    if (!contentNode.isMissingNode()) {
                        return Flux.just(contentNode.asText());
                    } else {
                        return Flux.empty();
                    }
                } catch (Exception e) {
                    // 解析异常跳过
                    log.warn("解析响应内容时出错", e);
                    return Flux.empty();
                }
            })
            .doOnNext(data -> log.debug("[接收到大模型数据] {}", data));  // 打印接收数据
    }

    @Override
    public String chat(List<ChatMessage> messages) {
        // TODO: 同步返回，主要涉及后续智能梗概功能
        return "";
    }

}
