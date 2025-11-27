package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình: Khi gọi URL http://domain/uploads/** // -> Sẽ tìm file trong thư mục "uploads/" tại thư mục gốc của dự án
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}