package cn.timflux.storyseek.ai.model;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;

import java.util.Map;
/**
 * ClassName: OpenAIStoryBeginningStrategy
 * Package: cn.timflux.storyseek.ai.model
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午6:52
 * @Version 1.0
 */

@Component
public class OpenAIStoryBeginningStrategy implements StoryBeginningStrategy {

    private final ChatClient chatClient;
    private final String systemPrompt;

    public OpenAIStoryBeginningStrategy(
        ChatClient.Builder chatClientBuilder,
        @Value("${story.prompt.beginning}") String systemPrompt
    ) {
        this.chatClient = chatClientBuilder.build();
        this.systemPrompt = systemPrompt;
    }

    @Override
    public Flux<String> generateBeginning(Map<String, Object> context) {
        String userPrompt = String.format(
            "%s\n角色: %s\n风格: %s\n世界观: %s\n其他要求: %s",
            systemPrompt,
            context.get("heroName"),
            context.get("styleTag"),
            context.get("worldSetting"),
            context.getOrDefault("otherReq", "无")
        );

        return chatClient.prompt()
                .system("你是一位富有想象力的故事作家。")
                .user(userPrompt)
                .stream()
                .content();
    }
}

