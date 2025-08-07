package cn.timflux.storyseek.common.metrics;

import java.lang.annotation.*;

/**
 * ClassName: Monitored
 * Package: cn.timflux.storyseek.common.metrics
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/8/4 下午3:40
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Monitored {
    /**
     * 指标名称（用于 Prometheus metrics 名称）
     */
    String value();

    /**
     * 可选的标签键值，例如 { "module=auth", "action=login" }
     */
    String[] tags() default {};
}
