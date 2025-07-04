package cn.timflux.storyseek.ai.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: ModelProvider
 * Package: cn.timflux.storyseek.ai.model
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/28 下午5:35
 * @Version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelProvider {
    String name();
}