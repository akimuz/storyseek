package cn.timflux.storyseek.ai.model;

import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * ClassName: StoryContinuationStrategy
 * Package: cn.timflux.storyseek.ai.model.core.story.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:41
 * @Version 1.0
 */
public interface StoryContinuationStrategy {
    Flux<String> generateContinuation(Map<String, Object> context);
}
