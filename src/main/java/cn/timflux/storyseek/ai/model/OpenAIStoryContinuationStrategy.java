package cn.timflux.storyseek.ai.model;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
/**
 * ClassName: OpenAIStoryContinuationStrategy
 * Package: cn.timflux.storyseek.ai.model
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:40
 * @Version 1.0
 */
@Component
public class OpenAIStoryContinuationStrategy implements StoryContinuationStrategy {

    private final ChatClient chatClient;
    private final String systemPrompt;

    public OpenAIStoryContinuationStrategy(
        ChatClient.Builder chatClientBuilder,
        @Value("${story.prompt.continuation}") String systemPrompt
    ) {
        this.chatClient = chatClientBuilder.build();  // 已是注入好的 ChatClient
        this.systemPrompt = systemPrompt;
    }

    @Override
    public Flux<String> generateContinuation(Map<String, Object> context) {
        String currentStory = (String) context.get("currentStory");
        String choiceId     = (String) context.get("choiceId");

        String userPrompt = String.format(
            "%s\n当前剧情:\n%s\n选项ID: %s\n\n"
          + "请先续写上述剧情，最后一行请输出标记 [[OPTIONS]]，然后紧跟一个 JSON 数组，包含两个选项对象 "
          + "(每个对象有 id 和 title 字段)，例如：\n"
          + "[{\"id\":\"A\",\"title\":\"...\"},{\"id\":\"B\",\"title\":\"...\"}]\n",
          systemPrompt, currentStory, choiceId
        );

        return chatClient.prompt()
                .system("你是一位擅长多分支叙事的 AI 作家。")
                .user(userPrompt)
                .stream()
                .content();
    }
}
