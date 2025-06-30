package cn.timflux.storyseek.ai.model.dto;

import lombok.Data;

/**
 * ClassName: ChatMessage
 * Package: cn.timflux.storyseek.ai.model
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/28 上午3:26
 * @Version 1.0
 */
@Data
public class ChatMessage {
    private String role;    // "user", "assistant", "system"
    private String content;

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // 推荐提供静态工厂方法，方便创建不同角色消息
    public static ChatMessage system(String content) {
        return new ChatMessage("system", content);
    }

    public static ChatMessage user(String content) {
        return new ChatMessage("user", content);
    }

    public static ChatMessage assistant(String content) {
        return new ChatMessage("assistant", content);
    }
}

