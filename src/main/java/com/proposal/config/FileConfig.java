package com.proposal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:D:/PAC_Proposals}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            String formattedPath = path.toAbsolutePath().toUri().toString();
            if (!formattedPath.endsWith("/")) {
                formattedPath += "/";
            }
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(formattedPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}