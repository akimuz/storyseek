package cn.timflux.storyseek.ai.model;

import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * ClassName: StoryEndingStrategy
 * Package: cn.timflux.storyseek.ai.model
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:41
 * @Version 1.0
 */
public interface StoryEndingStrategy {
    Flux<String> generateEnding(Map<String, Object> context);
}
