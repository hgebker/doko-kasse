package com.hgebk.doko.kasse.plugins.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private Environment environment;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowedOrigins("*").allowedHeaders("*");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // SPA fallback: forward non-asset, non-API paths to index.html so SvelteKit routing works
        // The regex [^.]* excludes paths with dots (JS/CSS/image assets) to let them resolve normally
        registry.addViewController("/{path:[^.]*}").setViewName("forward:/index.html");
        registry.addViewController("/{path:[^.]*}/**").setViewName("forward:/index.html");
    }
}
