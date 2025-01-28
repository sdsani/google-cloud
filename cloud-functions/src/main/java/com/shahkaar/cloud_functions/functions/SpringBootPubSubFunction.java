package com.shahkaar.cloud_functions.functions;

import com.shahkaar.cloud_functions.data.Constants;
import com.shahkaar.cloud_functions.data.PubSubBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;

@Slf4j
@SpringBootApplication
public class SpringBootPubSubFunction {

    @Bean
    public Function<PubSubBody.PubSubMessage, String> sbPubSubBGFunction() {
        return data -> {
            log.info(Constants.LINE);
            log.info("pubSubBody: {} ", new String(Base64.getDecoder().decode(data.getData()), StandardCharsets.UTF_8));
            log.info(Constants.LINE);
            return "Success";
        };
    }

    /*
    Result of following function is:
        {"data":"eyJpZCIgOiAiMTIzIiwicm9sZSIgOiAiQ2FwdGFpbiIsImZOYW1lIiA6ICJEb25hbGQiLCJsTmFtZSIgOiAiRHVjayJ9","message_id":"13698576184542791","publish_time":"2025-01-28T19:53:39.791Z"}
    @Bean
    public Function<String, String> sbPubSubBGFunction() {
        return data -> {
            log.info(Constants.LINE);
            log.info("data: {} ", data);
            log.info(Constants.LINE);
            return "Success";
        };
    }
     */
}

/*

Deploy to Google Run Functions:
    gcloud functions deploy pubsub-function-sb --gen2 \
        --project cloud-functions-448122 --region us-central1 --source=. --runtime=java21 \
        --entry-point=org.springframework.cloud.function.adapter.gcp.GcfJarLauncher \
        --trigger-topic=cloud-function-topic --allow-unauthenticated \
        --set-env-vars="SPRING_PROFILES_ACTIVE=sb-http-pubsub-function" \
        --set-env-vars="GOOGLE_FUNCTION_TARGET=apply" \
        --set-env-vars="MAIN_CLASS=com.shahkaar.cloud_functions.functions.SpringBootPubSubFunction" \
        --memory=512MB

Test
    drop a message in the topic
        gcloud pubsub topics publish cloud-function-topic \
            --message='{"id" : "123","role" : "Captain","fName" : "Donald","lName" : "Duck"}' \
            --project cloud-functions-448122

Delete function
    gcloud functions delete pubsub-function-sb --region us-central1 --project cloud-functions-448122
 */