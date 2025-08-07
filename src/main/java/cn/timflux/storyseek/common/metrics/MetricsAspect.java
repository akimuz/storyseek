package cn.timflux.storyseek.common.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * ClassName: MetricsAspect
 * Package: cn.timflux.storyseek.common.metrics
 * Description:
 * 结合注解的声明式拦截，用于针对接口方法手动埋点
 * @Author 一剑霜寒十四州
 * @Create 2025/8/4 下午3:42
 * @Version 1.0
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(cn.timflux.storyseek.common.metrics.Monitored)")
    public Object aroundMonitoredMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();

        Monitored annotation = method.getAnnotation(Monitored.class);
        String metricName = annotation.value();
        String[] tagPairs = annotation.tags();

        List<String> tags = new ArrayList<>();
        tags.add("method");
        tags.add(methodName); // 方法名作为默认 tag

        for (String tag : tagPairs) {
            String[] split = tag.split("=");
            if (split.length == 2) {
                tags.add(split[0]);
                tags.add(split[1]);
            }
        }

        long start = System.nanoTime();
        boolean success = true;

        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            success = false;
            throw ex;
        } finally {
            long duration = System.nanoTime() - start;
            tags.add("status");
            tags.add(success ? "success" : "error");

            Timer.builder(metricName)
                 .tags(tags.toArray(new String[0]))
                 .register(meterRegistry)
                 .record(duration, TimeUnit.NANOSECONDS);
        }
    }
}
