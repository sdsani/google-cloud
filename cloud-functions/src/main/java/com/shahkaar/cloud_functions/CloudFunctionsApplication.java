package com.shahkaar.cloud_functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.events.cloud.storage.v1.StorageObjectData;
import com.shahkaar.cloud_functions.converter.StorageObjectDataMessageConverter;
import com.shahkaar.cloud_functions.data.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.json.JacksonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;

import org.springframework.messaging.Message;

import java.util.function.Consumer;

@SpringBootApplication
@Slf4j
public class CloudFunctionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudFunctionsApplication.class, args);
	}

	@Bean
	public Consumer<Message<StorageObjectData>> consumer() {
		return message -> {
			log.info(Constants.LINE);
			StorageObjectData storageObjectData = message.getPayload();
			MessageHeaders mh = message.getHeaders();
			log.info(
					"Event on bucket={} name={}", storageObjectData.getBucket(), storageObjectData.getName());
			log.info("StorageObjectData: {}", storageObjectData );
			log.info("MessageHeaders: {}", mh );
			log.info(Constants.LINE);
		};
	}

	@Bean
	public MessageConverter messageConverter() {
		return new StorageObjectDataMessageConverter(new JacksonMapper(new ObjectMapper()));
	}
}

/*
Create function
	gcloud functions deploy storage-bg-function-sb2 --gen2 \
        --project cloud-functions-448122 --region us-central1 --source=. --runtime=java21 \
        --entry-point=org.springframework.cloud.function.adapter.gcp.GcfJarLauncher \
        --allow-unauthenticated \
        --set-env-vars="SPRING_PROFILES_ACTIVE=gcp" \
        --set-env-vars="GOOGLE_FUNCTION_TARGET=accept" \
        --set-env-vars="MAIN_CLASS=com.shahkaar.cloud_functions.CloudFunctionsApplication" \
        --trigger-event-filters="type=google.cloud.storage.object.v1.finalized" \
        --trigger-event-filters="bucket=event-function-test"

Delete function
    gcloud functions delete storage-bg-function-sb2 --region us-central1 --project cloud-functions-448122
 */