package com.xebia.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("com.xebia")
public class TestConfiguration {


    @Bean
    public ObjectMapper getJacksonObjectMapper() {
        MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();
        return jacksonMessageConverter.getObjectMapper();
    }
}
