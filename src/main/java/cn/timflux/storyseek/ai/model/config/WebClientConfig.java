package cn.timflux.storyseek.ai.model.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
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
    @Value("${webclient.connect-timeout:5000}")
    private int connectTimeout;
    @Value("${webclient.response-timeout:10000}")
    private int responseTimeout;
    @Value("${webclient.max-connections:500}")
    private int maxConnections;
    @Value("${webclient.pending-acquire-max:1000}")
    private int pendingAcquireMax;

    @Bean
    public WebClient webClient() {
        // 连接池配置
        ConnectionProvider connectionProvider = ConnectionProvider.builder("webclient-pool")
            .maxConnections(maxConnections)
            .pendingAcquireMaxCount(pendingAcquireMax)
            .pendingAcquireTimeout(Duration.ofSeconds(5))
            .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
            .responseTimeout(Duration.ofMillis(responseTimeout))
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(responseTimeout, TimeUnit.MILLISECONDS))
                .addHandlerLast(new WriteTimeoutHandler(responseTimeout, TimeUnit.MILLISECONDS))
            );

        if (proxyEnabled && proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0) {
            httpClient = httpClient.proxy(proxy -> proxy
                .type(ProxyProvider.Proxy.HTTP)
                .host(proxyHost)
                .port(proxyPort)
            );
            log.info("WebClient 配置了代理: {}:{}", proxyHost, proxyPort);
        }

        return WebClient.builder()
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
                values.forEach(value -> log.debug("Header  : {} = {}", name, value))
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
