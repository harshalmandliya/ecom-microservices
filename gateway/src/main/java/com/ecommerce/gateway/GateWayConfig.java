package com.ecommerce.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GateWayConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10,20,1);
    }

    @Bean
    public KeyResolver hostNameKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress().getHostName());
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("ecomBreaker")
                                        .setFallbackUri("forward:/fallback/products")
                                )
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNameKeyResolver())
                                )
                        )
                        .uri("http://localhost:8084")
                )

                .route("user-service",r->r
                        .path("/api/users/**")
                        .filters(f->f.circuitBreaker(config->config
                                .setName("ecomBreaker")
                                .setFallbackUri("forward:/fallback/users")
                        )

                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNameKeyResolver())
                                )

                        )
                        .uri("http://localhost:8083"))

                .route("order-service",r->r
                        .path("/api/orders/**","/api/cart/**")
                        .filters(f->f.circuitBreaker(config->config
                                .setName("ecomBreaker")
                                .setFallbackUri("forward:/fallback/orders")
                        )
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNameKeyResolver())
                                )
                        )
                        .uri("http://localhost:8085"))

                .route("eureka",r->r
                        .path("/eureka/main")
                        .filters(f->f.rewritePath("/eureka/main","/"))
                        .uri("http://localhost:8761"))

                .route("eureka-server-static",r->r
                        .path("/eureka/**")
                        .uri("http://localhost:8761"))
                .build();
    }
}
