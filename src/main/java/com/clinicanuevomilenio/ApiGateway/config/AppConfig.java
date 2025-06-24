package com.clinicanuevomilenio.ApiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // Bean de RestTemplate para realizar llamadas HTTP entre microservicios
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
