package com.shahkaar.cloud_functions.functions;

import com.google.cloud.functions.CloudEventsFunction;
import com.google.events.cloud.storage.v1.StorageObjectData;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class StorageEventFunction implements CloudEventsFunction {
    @Override
    public void accept(CloudEvent event) throws Exception {

        log.info("Event Type: {}", event.getType());

        if (event.getData() == null) {
            log.warn("No data found in cloud event payload!");
            return;
        }

        StorageObjectData storageObjectData = getDataObject(event);
        logStorageObjectData(storageObjectData);
    }

    private void logStorageObjectData(StorageObjectData data) {
        log.info("Bucket: {} ", data.getBucket());
        log.info("File: {} ", data.getName());
        log.info("Meta-generation: {}", data.getMetageneration());
        log.info("Created: {}", data.getTimeCreated());
        log.info("Updated: {}", data.getUpdated());
        log.info("Delete: {}", data.getTimeDeleted());
    }

    private StorageObjectData getDataObject(CloudEvent event) throws InvalidProtocolBufferException {
        String cloudEventData = new String(Objects.requireNonNull(event.getData()).toBytes(), StandardCharsets.UTF_8);
        StorageObjectData.Builder builder = StorageObjectData.newBuilder();

        JsonFormat.Parser parser = JsonFormat.parser().ignoringUnknownFields();
        parser.merge(cloudEventData, builder);
        return builder.build();
    }
}

/*
Storage Triggers: https://cloud.google.com/functions/docs/calling/storage

Deploy to Google Run Functions:
    gcloud functions deploy storage-event-function --gen2 \
        --project cloud-functions-448122 --region us-central1 --source=. --runtime=java21 \
        --entry-point=com.shahkaar.cloud_functions.functions.StorageEventFunction \
        --allow-unauthenticated \
        --trigger-event-filters="type=google.cloud.storage.object.v1.deleted" \
        --trigger-event-filters="bucket=event-function-test"

  Test Function:
    Delete a file from the bucket event-function-test

  Delete function
    gcloud functions delete storage-event-function --region us-central1 --project cloud-functions-448122

 */