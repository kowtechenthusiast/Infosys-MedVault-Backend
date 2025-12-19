package com.medibook.medibook_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This maps http://localhost:8080/files/** // to the physical directory C:/Users/.../IdeaProjects/Medibook/uploads/
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:uploads/");
    }
}