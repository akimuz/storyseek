package cn.timflux.storyseek.ai.model;
import cn.timflux.storyseek.ai.model.dto.ChatMessage;
import reactor.core.publisher.Flux;
import java.util.List;

/**
 * ClassName: ChatModelClient
 * Package: cn.timflux.storyseek.ai.model
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/28 上午3:26
 * @Version 1.0
 */
public interface ChatModel {
    Flux<String> streamChat(List<ChatMessage> messages);
    String chat(List<ChatMessage> messages);
}
