package cn.timflux.storyseek.ai.model;
import cn.timflux.storyseek.core.story.dto.OptionDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.timflux.storyseek.ai.model.OpenAIStoryContinuationStrategy.getChoiceTitle;

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

    @Autowired
    private final ChatClient chatClient;
    private final String systemPrompt;

    public OpenAIStoryEndingStrategy(
        ChatClient chatClient,
        @Value("${story.prompt.ending: default ending prompt}") String systemPrompt
    ) {
        this.chatClient = chatClient;
        this.systemPrompt = systemPrompt;
    }

    @Override
    public Flux<String> generateEnding(Map<String, Object> context) {
        String fullStory = (String) context.get("fullStory");
        List<?> rawOptions = (List<?>) context.get("lastOptions");
        String choiceId = (String) context.get("lastChoice");
        String currentStory = getChoiceTitle(rawOptions, choiceId);

        String userPrompt = String.format(
                """
                        %s
                        当前结尾剧情:
                        %s: %s

                        请根据当前选择结尾:
                        %s""",
            systemPrompt, choiceId, currentStory, fullStory
        );

        return chatClient.prompt()
                .system("你是一位擅长收束故事的 AI 作家。")
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, context.get("sessionId")))
                .user(userPrompt)
                .stream()
                .content();
    }
}
