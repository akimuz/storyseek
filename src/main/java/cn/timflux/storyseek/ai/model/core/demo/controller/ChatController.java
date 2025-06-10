package cn.timflux.storyseek.ai.model.core.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: ChatController
 * Package: cn.timflux.storyseek.ai.model.core.story.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午6:46
 * @Version 1.0
 */
@RestController
public class ChatController {

    //注入
    private final ChatClient chatClient;
    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    //1 实现简单对话功能
    @GetMapping("/chat")
    public String chat(@RequestParam(value = "msg",defaultValue = "你是谁")
                           String message) {
        return chatClient.prompt()    //提示词
                .user(message) //用户输入信息
                .call() //请求大模型
                .content(); //返回文本
    }
}
