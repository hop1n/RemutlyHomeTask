package com.remitly.test.calculator.cfg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CalculatorApplicationConfiguration {

    @Bean
    public RestTemplate myBean() {
        return new RestTemplate();
    }
}
