package com.sspu.config;

import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final String HEADER_NAME = "Access-Token";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求头
        ServerHttpRequest request = exchange.getRequest();
        // 获取响应头
        ServerHttpResponse response = exchange.getResponse();
        // 获取请求路径
        String url = request.getURI().getPath();
        // 获取请求头中的token
        String token = request.getHeaders().getFirst(HEADER_NAME);

        if(this.shouldNotFilter(url)){
            return chain.filter(exchange);
        }

        if(StringUtils.hasText(token)){
            return unAuthorize(exchange);
        }

        // 验证redis中是否有token
        if (redisTemplate.hasKey(token)) {
            return unAuthorize(exchange);
        }

        ServerHttpRequest newRequest = request.mutate().header(HEADER_NAME,token).build();
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return chain.filter(newExchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
    private boolean shouldNotFilter(String url) {
        return true;
    }

    private Mono<Void> unAuthorize(ServerWebExchange exchange) {
        // 设置响应状态码为401
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        // 设置响应头
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // 自定义错误信息
        String errMsg = "{\"error\":\""+"用户未登录或登录超时，请重新登录"+"\"}";
        // 将错误信息写入响应体
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errMsg.getBytes())));
    }
}