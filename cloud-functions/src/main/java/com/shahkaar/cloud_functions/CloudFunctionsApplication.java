package com.shahkaar.cloud_functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.events.cloud.storage.v1.StorageObjectData;
import com.shahkaar.cloud_functions.converter.StorageObjectDataMessageConverter;
import com.shahkaar.cloud_functions.data.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.json.JacksonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;

import org.springframework.messaging.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/*
	In this example all options listed below work.
		1. @Value annotation
		2. PubSubTemplate injection works
		3. PubSub message push works also.

	This means that we have spring boot dependency injection working in this case.

	So far, only time when I get working is when we have single main class. If we have more than one main classes
	either at the root or under the functions package, I get following error.
	Unable to find a single main class from the following candidates
		[com.shahkaar.cloud_functions.functions.SpringBootHttpFunctionWithMain, com.shahkaar.cloud_functions.functions.SpringBootBackgroundFunctionWithMain
	This will make code structure complicated and result lot of repos?
 */

@SpringBootApplication
@Slf4j
public class CloudFunctionsApplication {

	@Value("${spring-boot-environment}")
	String sbEnv;

	@Autowired
	PubSubTemplate pubSubTemplate;

	public static void main(String[] args) {
		SpringApplication.run(CloudFunctionsApplication.class, args);
	}

	@Bean
	public Function<Message<StorageObjectData>, String> storageMessageConsumer() {
		return message -> {
			StorageObjectData storageObjectData = message.getPayload();
			MessageHeaders mh = message.getHeaders();
			log.info(Constants.LINE);
			log.info("spring-boot-environment: {} ", sbEnv);
			log.info(
					"Event on bucket={} name={}", storageObjectData.getBucket(), storageObjectData.getName());
			log.info("StorageObjectData: {}", storageObjectData );
			log.info("MessageHeaders: {}", mh );
			log.info(Constants.LINE);
			pushMessage(storageObjectData);
			return "Success";
		};
	}

	private void pushMessage(StorageObjectData storageObjectData) {
		log.info("pushing: {} to topic: {} ", storageObjectData, Constants.TOPIC_NAME);
		CompletableFuture<String> future = pubSubTemplate.publish(Constants.TOPIC_NAME, storageObjectData.toString());
        try {
            String response = future.get();
			log.info("Result of Push (PubSub) : {}", response);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

	@Bean
	public MessageConverter messageConverter() {
		return new StorageObjectDataMessageConverter(new JacksonMapper(new ObjectMapper()));
	}
}

/*
Create function
	gcloud functions deploy storage-message-consumer-sb --gen2 \
        --project cloud-functions-448122 --region us-central1 --source=. --runtime=java21 \
        --entry-point=org.springframework.cloud.function.adapter.gcp.GcfJarLauncher \
        --allow-unauthenticated \
        --set-env-vars="SPRING_PROFILES_ACTIVE=gcp" \
        --set-env-vars="GOOGLE_FUNCTION_TARGET=accept" \
        --set-env-vars="MAIN_CLASS=com.shahkaar.cloud_functions.CloudFunctionsApplication" \
        --trigger-event-filters="type=google.cloud.storage.object.v1.finalized" \
        --trigger-event-filters="bucket=event-function-test" \
        --memory=512MB

Delete function
    gcloud functions delete storage-message-consumer-sb --region us-central1 --project cloud-functions-448122
 */