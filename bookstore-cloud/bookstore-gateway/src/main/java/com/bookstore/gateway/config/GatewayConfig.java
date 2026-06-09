package com.bookstore.gateway.config;

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
public class GatewayConfig {

    @PostConstruct
    public void init() {
        // 配置 Sentinel 连接 Dashboard
        System.setProperty("csp.sentinel.dashboard.server", "localhost:8880");
        System.setProperty("csp.sentinel.api.port", "8719");
        System.setProperty("project.name", "bookstore-gateway");
    }

    /**
     * 自定义 WebFilter，使用 Sentinel Core 做限流
     */
    @Bean
    public WebFilter sentinelWebFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            String path = exchange.getRequest().getPath().value();
            if (!path.startsWith("/api/public/")) {
                return chain.filter(exchange);
            }
            Entry entry = null;
            try {
                entry = SphU.entry(path, EntryType.IN);
                return chain.filter(exchange);
            } catch (BlockException ex) {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                String body = "{\"code\":429,\"message\":\"请求过于频繁，请稍后再试\",\"data\":null}";
                DataBuffer buffer = exchange.getResponse().bufferFactory()
                        .wrap(body.getBytes(StandardCharsets.UTF_8));
                return exchange.getResponse().writeWith(Mono.just(buffer));
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
        };
    }
}
