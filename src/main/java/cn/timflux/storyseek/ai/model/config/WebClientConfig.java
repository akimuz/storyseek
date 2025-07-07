package cn.timflux.storyseek.ai.model.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
/**
 * ClassName: WebClientConfig
 * Package: cn.timflux.storyseek.ai.model.config
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午10:52
 * @Version 1.0
 */
@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${proxy.enabled:false}") // 默认不启用代理
    private boolean proxyEnabled;
    @Value("${proxy.host}")
    private String proxyHost;
    @Value("${proxy.port}")
    private int    proxyPort;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create();
        if (proxyEnabled && proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0){
            // Reactor Netty HttpClient 设置代理
            httpClient = HttpClient.create()
                .proxy(proxySpec -> proxySpec
                    .type(ProxyProvider.Proxy.HTTP)
                    .host(proxyHost)
                    .port(proxyPort)
                );
        }

        return builder
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .filter(logRequest())   // 打印请求
            .filter(logResponse())  // 打印响应
            .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("=== [WebClient Request] ===");
            log.debug("URL     : {}", clientRequest.url());
            log.debug("Method  : {}", clientRequest.method());
            clientRequest.headers().forEach((name, values) ->
                values.forEach(value -> log.info("Header  : {} = {}", name, value))
            );
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.debug("=== [WebClient Response] ===");
            log.debug("Status code: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

}
