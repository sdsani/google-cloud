package com.shahkaar.cloud_functions.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shahkaar.cloud_functions.converter.StorageObjectDataMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.function.json.JacksonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

//@Configuration
@Slf4j
public class MessageConverterConfig {

    @Bean
    public MessageConverter messageConverter() {
        log.info("================================> MessageConverter created <<<<<<<<");
        return new StorageObjectDataMessageConverter(new JacksonMapper(new ObjectMapper()));
    }
}
