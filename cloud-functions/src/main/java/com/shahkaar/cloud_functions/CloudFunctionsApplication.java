package com.shahkaar.cloud_functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shahkaar.cloud_functions.converter.StorageObjectDataMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.json.JacksonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MessageConverter;

@SpringBootApplication
@Slf4j
public class CloudFunctionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudFunctionsApplication.class, args);
	}

	@Bean
	public MessageConverter messageConverter() {
		log.info("================================> MessageConverter created ^^^^^^^^^^");
		return new StorageObjectDataMessageConverter(new JacksonMapper(new ObjectMapper()));
	}
}
