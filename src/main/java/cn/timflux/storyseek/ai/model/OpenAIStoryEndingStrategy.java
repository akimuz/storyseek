package cn.timflux.storyseek.ai.model;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * ClassName: OpenAIStoryEndingStrategy
 * Package: cn.timflux.storyseek.ai.model.core.story.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:43
 * @Version 1.0
 */
@Component
public class OpenAIStoryEndingStrategy implements StoryEndingStrategy {

    private final ChatClient chatClient;
    private final String systemPrompt;

    public OpenAIStoryEndingStrategy(
        ChatClient.Builder chatClientBuilder,
        @Value("${story.prompt.ending}") String systemPrompt
    ) {
        this.chatClient = chatClientBuilder.build();
        this.systemPrompt = systemPrompt;
    }

    @Override
    public Flux<String> generateEnding(Map<String, Object> context) {
        String fullStory = (String) context.get("fullStory");

        String userPrompt = String.format(
            "%s\n完整剧情摘要:\n%s",
            systemPrompt,
            fullStory
        );

        return chatClient.prompt()
                .system("你是一位擅长收束故事的 AI 作家。")
                .user(userPrompt)
                .stream()
                .content();
    }
}
