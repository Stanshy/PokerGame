package com.chris.poker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://poker-game-vue.vercel.app",
            		"http://localhost:5173")  // 允許的前端網域
            .allowedMethods("*")
            .allowCredentials(true)
            .allowedHeaders("*");
    }
}