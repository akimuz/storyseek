package cn.timflux.storyseek.ai.service;

import cn.timflux.storyseek.ai.model.ChatModel;
import cn.timflux.storyseek.ai.model.ModelProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: ChatModelFactory
 * Package: cn.timflux.storyseek.ai.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/28 上午3:27
 * @Version 1.0
 */
@Component
public class ChatModelFactory {

    private final ApplicationContext context;

    public ChatModelFactory(ApplicationContext context) {
        this.context = context;
    }

    private final Map<String, ChatModel> registry = new HashMap<>();

    @PostConstruct
    public void registerModels() {
        Map<String, ChatModel> beans = context.getBeansOfType(ChatModel.class);
        for (ChatModel model : beans.values()) {
            ModelProvider annotation = model.getClass().getAnnotation(ModelProvider.class);
            if (annotation != null) {
                registry.put(annotation.name().toLowerCase(), model);
            }
        }
    }

    public ChatModel getModel(String provider) {
        ChatModel model = registry.get(provider.toLowerCase());
        if (model == null) {
            throw new IllegalArgumentException("Unknown model provider: " + provider);
        }
        return model;
    }

}
