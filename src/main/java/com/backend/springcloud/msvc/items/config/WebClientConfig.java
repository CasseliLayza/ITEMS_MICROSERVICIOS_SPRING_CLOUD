package com.backend.springcloud.msvc.items.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    //@Value("${http://msvc-products/products}")
    @Value("${config.base-url.endpoint.msvc-products}")
    private String url;

    @PostConstruct
    public void validateUrl() {
        if (url == null || url.contains("${") || !url.startsWith("http")) {
            System.out.println("url = " + url);
            throw new IllegalStateException("❌ La propiedad 'config.base-url.endpoint.msvc-products' no fue resuelta correctamente. Verifica tu archivo application.properties o application.yml.");
        }
        System.out.println("✅ URL correctamente cargada: " + url);
    }

/*    @Bean
    @LoadBalanced
    WebClient.Builder webClient() {
        return WebClient.builder().baseUrl(url);
    }*/


    @Bean
    WebClient webClient(WebClient.Builder webClientBuilder, ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        return webClientBuilder.baseUrl(url).filter(lbFunction).build();
    }
}
