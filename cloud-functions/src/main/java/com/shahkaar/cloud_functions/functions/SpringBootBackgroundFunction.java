package com.shahkaar.cloud_functions.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.events.cloud.storage.v1.StorageObjectData;
import com.shahkaar.cloud_functions.converter.StorageObjectDataMessageConverter;
import com.shahkaar.cloud_functions.data.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.json.JacksonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Slf4j
@SpringBootApplication
public class SpringBootBackgroundFunction {

    @Value("${spring-boot-environment}")
    String sbEnv;

    @Autowired
    PubSubTemplate pubSubTemplate;

    @Bean
    public Function<Message<StorageObjectData>, String> sbBackgroundFunction() {
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
    @Profile("sb-bg-function")
    public MessageConverter messageConverter() {
        return new StorageObjectDataMessageConverter(new JacksonMapper(new ObjectMapper()));
    }
}

/*
  Deploy to Google Run Functions:
    gcloud functions deploy storage-bg-function-sb --gen2 \
        --project cloud-functions-448122 --region us-central1 --source=. --runtime=java21 \
        --entry-point=org.springframework.cloud.function.adapter.gcp.GcfJarLauncher \
        --allow-unauthenticated \
        --set-env-vars="SPRING_PROFILES_ACTIVE=sb-bg-function" \
        --set-env-vars="GOOGLE_FUNCTION_TARGET=accept" \
        --set-env-vars="MAIN_CLASS=com.shahkaar.cloud_functions.functions.SpringBootBackgroundFunction" \
        --trigger-event-filters="type=google.cloud.storage.object.v1.finalized" \
        --trigger-event-filters="bucket=event-function-test" \
        --memory=512MB

  Test Function:
    Drop a file in the bucket event-function-test

  Delete function
    gcloud functions delete storage-bg-function-sb --region us-central1 --project cloud-functions-448122
 */
