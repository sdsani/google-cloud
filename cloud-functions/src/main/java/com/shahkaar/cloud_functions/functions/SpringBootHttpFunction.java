package com.shahkaar.cloud_functions.functions;

import static com.shahkaar.cloud_functions.data.Constants.*;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.shahkaar.cloud_functions.data.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Slf4j
//@Component
public class SpringBootHttpFunction implements Function<Employee, Employee> {

    @Value("${spring-boot-environment}")
    String sbEnv;

    // If we inject pubSubTemplate here then we get an error saying missing bean
    // If I add @Component annotation, still I get this error. However, this works when we add it to the main class
    //@Autowired
    //PubSubTemplate pubSubTemplate;

    @Override
    public Employee apply(Employee employee) {
        log.info(LINE);
        log.info("Data received: {}", employee.toString());
        log.info("Spring boot Variable: {}", sbEnv);
        employee.setId(reverse(employee.getId()));
        employee.setFName(reverse(employee.getFName()));
        employee.setLName(reverse(employee.getLName()));
        employee.setRole(reverse(employee.getRole()));
        log.info(employee.toString());
        //pushMessage("Hello PubSub");
        log.info(LINE);
        return employee;
    }

    private String reverse(String data) {
        return new StringBuilder(data).reverse().toString();
    }

//    private void pushMessage(String message) {
//        log.info("pushing: {} to topic: {} ", message, TOPIC_NAME);
//        CompletableFuture<String> future = pubSubTemplate.publish(TOPIC_NAME, message);
//
//        try {
//            String response = future.get();
//            log.info("Result of Push (PubSub) : {}", response);
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//    }

}

/*
 Deploy to emulator:
    gcloud alpha functions local deploy http-function-sb \
        --entry-point=org.springframework.cloud.function.adapter.gcp.GcfJarLauncher \
        --runtime=java21 --source=. --port=8080 \
        --set-env-vars="SPRING_PROFILES_ACTIVE=local" \
        --set-env-vars="GOOGLE_FUNCTION_SIGNATURE_TYPE=http" \
        --set-env-vars="GOOGLE_FUNCTION_TARGET=apply" \
        --set-env-vars="MAIN_CLASS=com.shahkaar.cloud_functions.functions.SpringBootHttpFunction"
        --project cloud-functions

  Deploy to Google Run Functions:
    gcloud functions deploy http-function-sb --gen2 \
        --project cloud-functions-448122 --region us-central1 --source=. --runtime=java21 \
        --entry-point=org.springframework.cloud.function.adapter.gcp.GcfJarLauncher \
        --trigger-http --allow-unauthenticated \
        --set-env-vars="SPRING_PROFILES_ACTIVE=gcp" \
        --set-env-vars="GOOGLE_FUNCTION_TARGET=apply" \
        --set-env-vars="MAIN_CLASS=com.shahkaar.cloud_functions.functions.SpringBootHttpFunction" \
        --memory=512MB

  Test Function:
    curl --header "Content-Type: application/json" \
        --request POST \
        --data '{"id" : "123","role" : "Captain","fName" : "Donald","lName" : "Duck"}' \
        https://us-central1-cloud-functions-448122.cloudfunctions.net/http-function-sb

  Delete function
    gcloud functions delete http-function-sb --region us-central1 --project cloud-functions-448122
    gcloud functions list --project cloud-functions-448122

 */