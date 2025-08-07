package cn.timflux.storyseek.common.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/**
 * ClassName: MetricsAopAspect
 * Package: cn.timflux.storyseek.common.metrics
 * Description:
 * 对Http请求的接口通用拦截，用于监控
 * @Author 一剑霜寒十四州
 * @Create 2025/8/7 上午11:52
 * @Version 1.0
 */

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsHttpAspect {

    private final MeterRegistry meterRegistry;

    // 白名单路径前缀，不做监控或单独监控的接口放这里排除
    private static final Set<String> EXCLUDE_PATH_PREFIXES = Set.of(
            "/api/write/stream", "/actuator", "/favicon.ico", "/static", "/error"
    );

    /**
     * 切面拦截所有 Controller 的请求方法
     * 拦截所有 @RequestMapping、@GetMapping、@PostMapping 等
     */
    @Around("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    public Object aroundController(ProceedingJoinPoint pjp) throws Throwable {

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            // 非HTTP请求，直接执行
            return pjp.proceed();
        }

        HttpServletRequest request = attrs.getRequest();
        HttpServletResponse response = attrs.getResponse();

        String path = request.getRequestURI();

        if (EXCLUDE_PATH_PREFIXES.stream().anyMatch(path::startsWith)) {
            return pjp.proceed();
        }

        long startNano = System.nanoTime();

        boolean success = true;
        Object ret;
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

        try {
            ret = pjp.proceed();
            // HTTP状态优先从response获取,4xx/5xx 视为失败，400以下视为成功
            status = response != null ? response.getStatus() : HttpStatus.OK.value();
            success = (status >= 200 && status < 400);
        } catch (Throwable ex) {
            success = false;
            throw ex;
        } finally {
            long durationNs = System.nanoTime() - startNano;

            String method = request.getMethod();
            String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            if (pattern == null) {
                log.debug("[MetricsAopAspect] 未获取到标准化 URI，使用原始路径: {}", path);
                pattern = path;
            }

            Timer.builder("storyseek_http_server_requests_seconds")
                    .publishPercentileHistogram()
                    .minimumExpectedValue(Duration.ofMillis(1))
                    .maximumExpectedValue(Duration.ofSeconds(10))
                    .tags(
                            "method", method,
                            "uri", pattern,
                            "status", String.valueOf(status),
                            "success", String.valueOf(success)
                    )
                    .description("HTTP Server Requests")
                    .register(meterRegistry)
                    .record(durationNs, TimeUnit.NANOSECONDS);

            log.debug("[MetricsAopAspect] 监控数据：method={}, uri={}, status={}, success={}, duration={}ms",
                    method, pattern, status, success, durationNs / 1_000_000);
        }

        return ret;
    }
}
