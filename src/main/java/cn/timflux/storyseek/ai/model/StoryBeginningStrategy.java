package cn.timflux.storyseek.ai.model;

import java.util.Map;

/**
 * ClassName: StoryBeginningStrategy
 * Package: cn.timflux.storyseek.ai.model
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/9 下午9:15
 * @Version 1.0
 */
public interface StoryBeginningStrategy {
    String generateBeginning(Map<String, Object> context);
}
