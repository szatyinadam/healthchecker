package com.balloon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * Created by aszatyin on 2017-03-14.
 */
@Component
public class RestTemplateConfig {

    @Value("${app.check.url.username}")
    private String username;

    @Value("${app.check.url.password}")
    private String password;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));

        return builder.additionalMessageConverters(mappingJackson2HttpMessageConverter).basicAuthentication(username, password).build();
    }
}
