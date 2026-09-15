package com.ecommerce.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GateWayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service",r->r
                                .path("/api/products/**")
                                .filters(f->f.circuitBreaker(config->config
                                        .setName("ecomBreaker")
                                        .setFallbackUri("forward:/fallback/products")
                                ))
                                .uri("http://localhost:8084"))

                .route("user-service",r->r
                        .path("/api/users/**")
                        .filters(f->f.circuitBreaker(config->config
                                .setName("ecomBreaker")
                                .setFallbackUri("forward:/fallback/users")
                        ))
                        .uri("http://localhost:8083"))

                .route("order-service",r->r
                        .path("/api/orders/**","/api/cart/**")
                        .filters(f->f.circuitBreaker(config->config
                                .setName("ecomBreaker")
                                .setFallbackUri("forward:/fallback/orders")
                        ))
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
