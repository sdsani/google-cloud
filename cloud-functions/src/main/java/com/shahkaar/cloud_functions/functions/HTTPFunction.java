package com.shahkaar.cloud_functions.functions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
public class HTTPFunction implements HttpFunction {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {

        log.info("requestContentType: {} ", httpRequest.getContentType());
        log.info("requestContentLength: {} ", httpRequest.getContentLength());
        String data = new String(httpRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        log.info("data: {} ", data);
        httpResponse.getWriter().write(data);
    }
}

/*
Deploy:
    gcloud functions deploy http-function --gen2 \
        --project cloud-functions-448122 --region us-central1 --source=. --runtime=java21 \
        --entry-point=com.shahkaar.cloud_functions.functions.HTTPFunction \
        --trigger-http --allow-unauthenticated

Test
    curl --header "Content-Type: application/json" \
        --request POST \
        --data '{"id" : "123","role" : "Captain","fName" : "Donald","lName" : "Duck"}' \
        https://us-central1-cloud-functions-448122.cloudfunctions.net/http-function

Delete function
    gcloud functions delete http-function --region us-central1 --project cloud-functions-448122
 */