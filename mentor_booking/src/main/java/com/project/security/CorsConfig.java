package com.project.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Chỉ cho phép truy cập từ frontend (địa chỉ của bạn)
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "DELETE", "PUT")
                        .allowedOrigins("http://localhost:5173");// Địa chỉ frontend của bạn
            }
        };
    }
}
