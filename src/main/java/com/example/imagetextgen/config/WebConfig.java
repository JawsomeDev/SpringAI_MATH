package com.example.imagetextgen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")  // URL 패턴
                .addResourceLocations("file:" + uploadPath);  // 파일 시스템의 실제 경로
        // src/main/resources/static/uploads/
    }
}

/*
   //                                    /uploads/**
  <img src="http://localhost:8080/uploads/454555454.png"/>

 */
