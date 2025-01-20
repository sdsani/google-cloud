package com.shahkaar.cloud_functions.functions;

import static com.shahkaar.cloud_functions.data.Constants.*;

import com.google.events.cloud.storage.v1.StorageObjectData;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

// https://medium.com/@claudiorauso/storage-triggered-google-cloud-functions-in-java-ebadaf6e2943
// 1. You must enable eventarc.googleapis.com api to use this option.
// 2. To use GCS CloudEvent triggers, the GCS service account requires the Pub/Sub Publisher (roles/pubsub.publisher)
//      IAM role in the specified project. (See https://cloud.google.com/eventarc/docs/run/quickstart-storage#before-you-begin):
//      permission denied" },
//     To solve this issue. use commands below
//     gcloud storage service-agent --project=cloud-functions-448122 => service-177997074765@gs-project-accounts.iam.gserviceaccount.com
//
//     gcloud projects add-iam-policy-binding cloud-functions-448122 \
//          --member="serviceAccount:service-177997074765@gs-project-accounts.iam.gserviceaccount.com" \
//          --role='roles/pubsub.publisher'

@Slf4j
//public class SpringBootBackgroundFunction implements Consumer<StorageObjectData> {
//public class SpringBootBackgroundFunction implements Consumer<String> {
public class SpringBootBackgroundFunction implements Consumer<String> {
    @Override
    //public void accept(StorageObjectData storageObjectData) {
    //public void accept(String storageObjectData) {
    public void accept(String data) {
        log.info(LINE);
        StorageObjectData storageObjectData = transform(data);
        log.info("startObjectData: {} ", storageObjectData);
        log.info(LINE);
    }

    private StorageObjectData transform(String data) {
        StorageObjectData.Builder builder = StorageObjectData.newBuilder();
        try {
            JsonFormat.parser().merge(data, builder);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        return builder.build();
    }

}

/*
  Deploy to Google Run Functions:
    gcloud functions deploy storage-bg-function-sb --gen2 \
        --project cloud-functions-448122 --region us-central1 --source=. --runtime=java21 \
        --entry-point=org.springframework.cloud.function.adapter.gcp.GcfJarLauncher \
        --allow-unauthenticated \
        --set-env-vars="SPRING_PROFILES_ACTIVE=gcp" \
        --set-env-vars="GOOGLE_FUNCTION_TARGET=accept" \
        --set-env-vars="MAIN_CLASS=com.shahkaar.cloud_functions.functions.SpringBootBackgroundFunction" \
        --trigger-event-filters="type=google.cloud.storage.object.v1.finalized" \
        --trigger-event-filters="bucket=event-function-test"

  Test Function:
    Drop a file in the bucket event-function-test

  Delete function
    gcloud functions delete storage-bg-function-sb --region us-central1 --project cloud-functions-448122
    gcloud functions list --project cloud-functions-448122
 */
