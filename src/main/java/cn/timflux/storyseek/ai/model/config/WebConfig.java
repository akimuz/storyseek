package cn.timflux.storyseek.ai.model.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Instant;

/**
 * ClassName: WebConfig
 * Package: cn.timflux.storyseek.ai.model.config
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午10:32
 * @Version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:5173",
                    "https://www.timeflux.cn",
                    "https://timeflux.cn")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 缓存禁用
        registry.addInterceptor(new NoCacheInterceptor())
                .addPathPatterns("/auth/status");

        // SSE 响应头拦截器
        registry.addInterceptor(new SseResponseInterceptor())
                .addPathPatterns("/api/write/stream");
    }

    private static class NoCacheInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setDateHeader("Last-Modified", Instant.now().toEpochMilli());
            return true;
        }
    }
}