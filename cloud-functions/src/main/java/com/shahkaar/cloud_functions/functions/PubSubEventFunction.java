package com.shahkaar.cloud_functions.functions;

import com.google.cloud.functions.CloudEventsFunction;
import com.google.gson.Gson;
import com.shahkaar.cloud_functions.data.Constants;
import io.cloudevents.CloudEvent;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import com.shahkaar.cloud_functions.data.PubSubBody;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PubSubEventFunction implements CloudEventsFunction {
    @Override
    public void accept(CloudEvent event) throws Exception {

        String cloudEventData = new String(Objects.requireNonNull(event.getData()).toBytes(), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        PubSubBody body = gson.fromJson(cloudEventData, PubSubBody.class);

        String encodedData = body.getMessage().getData();

        String decodedData = new String(Base64.getDecoder().decode(encodedData), StandardCharsets.UTF_8);
        log.info(Constants.LINE);
        log.info("DecodedPubSubData: {}", decodedData);
        log.info(Constants.LINE);
    }
}

/*

Deploy to Google Run Functions:
    gcloud functions deploy pubsub-event-function --gen2 \
        --project cloud-functions-448122 --region us-central1 --source=. --runtime=java21 \
        --entry-point=com.shahkaar.cloud_functions.functions.PubSubEventFunction \
        --allow-unauthenticated \
        --trigger-topic=cloud-function-topic \
        --memory=256MB

  Test Function:
    Insert a file in Storage bucket
    This will trigger SpringBootBackgroundFunction
    SBBGFunction will push a message (of type StorageObjectData) into pubsub topic

    Or simply drop a message in the topic
        gcloud pubsub topics publish cloud-function-topic --message="Hello PubSub function" \
            --project cloud-functions-448122

  Delete function
    gcloud functions delete pubsub-event-function --region us-central1 --project cloud-functions-448122

 */