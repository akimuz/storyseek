package cn.timflux.storyseek.ai.model;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    private final ChatClient chatClient;
    private final String systemPrompt;

    public OpenAIStoryBeginningStrategy(
        ChatClient chatClient,
        @Value("${story.prompt.beginning: 总字数不超过50字}") String systemPrompt
    ) {
        this.chatClient = chatClient;
        this.systemPrompt = systemPrompt;
    }

    @Override
    public Flux<String> generateBeginning(Map<String, Object> context) {
        String userPrompt = String.format(
            "%s\n角色: %s\n风格: %s\n世界观: %s\n其他要求: %s"
          + "请根据上述设定写作，最后一行请输出标记 [[OPTIONS]]，然后紧跟一个 JSON 数组，包含两个选项对象 "
          + "(每个对象有 id 和 title 字段)，例如：\n"
          + "[{\"id\":\"A\",\"title\":\"...\"},{\"id\":\"B\",\"title\":\"...\"}]\n",
          systemPrompt,
            context.get("heroName"),
            context.get("styleTag"),
            context.get("worldSetting"),
            context.getOrDefault("otherReq", "无")
        );
        System.out.println(userPrompt);
        Flux<String> res = chatClient.prompt()
                .user(userPrompt)
                .stream()
                .content();
        System.out.println(res);
        return res;
    }
}

