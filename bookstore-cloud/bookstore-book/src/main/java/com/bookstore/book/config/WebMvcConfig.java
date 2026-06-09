package com.bookstore.book.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File uploadDir = new File(uploadPath).getAbsoluteFile();
        if (!uploadDir.exists() || !uploadDir.isDirectory()) {
            File alt = new File("bookstore-cloud", uploadPath).getAbsoluteFile();
            if (alt.exists() && alt.isDirectory()) {
                uploadDir = alt;
            }
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
