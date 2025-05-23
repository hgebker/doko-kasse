package com.hgebk.doko.kasse.plugins.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CustomRequestLogFilter filter() {
        CustomRequestLogFilter filter = new CustomRequestLogFilter();
        filter.setBeforeMessagePrefix("DBACK: ");
        filter.setAfterMessagePrefix("DBACK: ");
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        return filter;
    }
}
