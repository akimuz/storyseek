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

    @Autowired
    private final ChatClient chatClient;
    private final String systemPrompt;

    public OpenAIStoryContinuationStrategy(
        ChatClient chatClient,
        @Value("${story.prompt.continuation:default continuation prompt}") String systemPrompt
    ) {
        this.chatClient = chatClient;  // 已是注入好的 ChatClient
        this.systemPrompt = systemPrompt;
    }

    @Override
    public Flux<String> generateContinuation(Map<String, Object> context) {
        try {
            List<?> rawOptions = (List<?>) context.get("lastOptions");
            String choiceId = (String) context.get("lastChoice");
            String currentStory = getChoiceTitle(rawOptions, choiceId);

            String userPrompt = String.format(
                    """
                            %s
                            选择剧情:
                            %s: %s

                            请根据选择剧情续写，最后一行请输出标记 &，然后紧跟一个 JSON 数组，包含两个选项对象 \
                            (每个对象有 id 和 title 字段)，例如：
                            [{"id":"A","title":"..."},{"id":"B","title":"..."}]
                            """,
              systemPrompt, choiceId, currentStory
            );
            System.out.println("context:"+context);
            return chatClient.prompt()
                    .system("你是一位擅长多分支叙事的 AI 作家。")
                    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, context.get("sessionId")))
                    .user(userPrompt)
                    .stream()
                    .content();
        } catch (ClassCastException | IllegalArgumentException e) {
        return Flux.error(new IllegalStateException("上下文数据异常: " + e.getMessage()));
        }
    }

    // 获取选择的选项内容 包含类型安全检查
    public static String getChoiceTitle(List<?> rawOptions, String choiceID) {
        if (rawOptions == null) {
            throw new IllegalArgumentException("lastOptions不能为null");
        }
        return rawOptions.stream()
            .filter(OptionDTO.class::isInstance) // 过滤非OptionDTO对象
            .map(OptionDTO.class::cast)
            .filter(opt -> choiceID.equals(opt.getId()))
            .findFirst()
            .map(OptionDTO::getTitle)
            .orElseThrow(() -> new IllegalArgumentException(
                "找不到匹配选项: choiceID=" + choiceID +
                " | 有效选项: " + rawOptions.stream()
                    .filter(OptionDTO.class::isInstance)
                    .map(o -> ((OptionDTO)o).getId())
                    .collect(Collectors.joining(","))
            ));
    }
}
