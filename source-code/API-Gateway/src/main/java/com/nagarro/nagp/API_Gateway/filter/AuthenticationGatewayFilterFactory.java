package com.nagarro.nagp.API_Gateway.filter;

import com.nagarro.nagp.API_Gateway.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationGatewayFilterFactory.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private RouteValidator validator;

    public AuthenticationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return handleAuthenticationError(exchange, new AuthenticationException("Missing Authorization Header"));
                }

                String token = authHeader.substring(7);

                return validateToken(token)
                        .flatMap(user -> proceedWithUser(user, exchange, chain))
                        .onErrorResume(e -> handleAuthenticationError(exchange, e));
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Map<String, Object>> validateToken(String token) {
        return webClientBuilder.build()
                .post()
                .uri("http://auth-service/auth/validate-token?token=" + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(Collections.singletonMap("token", token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    private Mono<Void> proceedWithUser(Map<String, Object> user, ServerWebExchange exchange, GatewayFilterChain chain) {
        String username = user.get("username").toString();
        List<String> roles = (List<String>) user.get("roles");
        String employeeCode = user.get("employeeCode").toString();
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-USERNAME", username)
                .header("X-ROLES", String.join(",", roles))
                .header("X-EMPLOYEE-CODE", employeeCode)
                .build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private Mono<Void> handleAuthenticationError(ServerWebExchange exchange, Throwable e) {
        logger.error("Authentication failed: {}", e.getMessage());
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {}
}
