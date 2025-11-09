package com.trip.apigateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class AuthFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        var request = exchange.getRequest();
//
//        if (!request.getHeaders().containsKey("Authorization")) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//
//        String authHeader = request.getHeaders().getFirst("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        String token = authHeader.substring(7); // remove "Bearer "
//
//
//
//
//        return chain.filter(exchange);
        return null;
    }

}
