package com.coding.tinder.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.constraints.NotNull;

/**
 * @author samuel
 */
@Configuration
public class CorsConfiguration {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowCredentials(true)
                        .allowedMethods("*")
                        .allowedOrigins("http://120.46.54.54","http://127.0.0.1:5173","http://localhost:5173","http://localhost:8000","http://localhost:8082")
                        .maxAge(3600);
            }
        };
    }

}
