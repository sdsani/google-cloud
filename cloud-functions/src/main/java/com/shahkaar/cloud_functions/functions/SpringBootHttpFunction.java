package com.shahkaar.cloud_functions.functions;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.shahkaar.cloud_functions.data.Constants;
import com.shahkaar.cloud_functions.data.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static com.shahkaar.cloud_functions.data.Constants.LINE;

@Slf4j
@SpringBootApplication
public class SpringBootHttpFunction {

    @Value("${spring-boot-environment}")
    String sbEnv;

    @Autowired
    PubSubTemplate pubSubTemplate;

    @Bean
    public Function<Employee, Employee> sbHttpFunction() {
        return employee -> {

            log.info(LINE);
            log.info("Data received: {}", employee.toString());
            log.info("Spring boot Variable: {}", sbEnv);
            employee.setId(reverse(employee.getId()));
            employee.setFName(reverse(employee.getFName()));
            employee.setLName(reverse(employee.getLName()));
            employee.setRole(reverse(employee.getRole()));
            log.info(employee.toString());
            pushMessage(employee);
            log.info(LINE);
            return employee;
        };
    }

    private String reverse(String data) {
        return new StringBuilder(data).reverse().toString();
    }

    private void pushMessage(Employee employee) {
        log.info("pushing: {} to topic: {} ", employee, Constants.TOPIC_NAME);
        CompletableFuture<String> future = pubSubTemplate.publish(Constants.TOPIC_NAME, employee.toString());

        try {
            String response = future.get();
            log.info("Result of Push (PubSub) : {}", response);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}

/*
Deploy to Google Run Functions:
    gcloud functions deploy http-function-sb --gen2 \
        --project cloud-functions-448122 --region us-central1 --source=. --runtime=java21 \
        --entry-point=org.springframework.cloud.function.adapter.gcp.GcfJarLauncher \
        --trigger-http --allow-unauthenticated \
        --set-env-vars="SPRING_PROFILES_ACTIVE=sb-http-function" \
        --set-env-vars="GOOGLE_FUNCTION_TARGET=apply" \
        --set-env-vars="MAIN_CLASS=com.shahkaar.cloud_functions.functions.SpringBootHttpFunction" \
        --memory=512MB

Test
    curl --header "Content-Type: application/json" \
        --request POST \
        --data '{"id" : "123","role" : "Captain","fName" : "Donald","lName" : "Duck"}' \
        https://us-central1-cloud-functions-448122.cloudfunctions.net/http-function-sb

Delete function
    gcloud functions delete http-function-sb --region us-central1 --project cloud-functions-448122
 */