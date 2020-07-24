package org.lemanoman.videoviz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    @Value("${gui.location}")
    private String guiLocation;

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/list").setViewName("list");

    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //registry.addResourceHandler("/gui/**").addResourceLocations("classpath:/gui/");
        Resource resource = new FileSystemResource(new File(guiLocation));
        String resourceLocation = null;
        try {
            resourceLocation = resource.getURL().toExternalForm();
            registry.addResourceHandler("/gui/**").addResourceLocations(resourceLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
