package com.shahkaar.cloud_functions.functions;

import com.shahkaar.cloud_functions.data.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.function.Function;

@Slf4j
public class HttpFunctionSpringBoot implements Function<Employee, Employee> {

    @Value("spring-boot-environment")
    String sbEnv;

    @Override
    public Employee apply(Employee employee) {
        log.info("===========================================================================================");
        log.info("Data received: {}", employee.toString());
        log.info("Spring boot Variable: {}", sbEnv);
        employee.setId(reverse(employee.getId()));
        employee.setFName(reverse(employee.getFName()));
        employee.setLName(reverse(employee.getLName()));
        employee.setRole(reverse(employee.getRole()));
        log.info(employee.toString());
        log.info("===========================================================================================");
        return employee;
    }

    private String reverse(String data) {
        return new StringBuilder(data).reverse().toString();
    }
}

/*
 Deploy to emulator:
    gcloud alpha functions local deploy http-function-sb \
        --entry-point=org.springframework.cloud.function.adapter.gcp.GcfJarLauncher \
        --runtime=java21 --source=. --port=8080 \
        --set-env-vars="spring_profile_active=local" \
        --set-env-vars="GOOGLE_FUNCTION_SIGNATURE_TYPE=http" \
        --set-env-vars="GOOGLE_FUNCTION_TARGET=apply" \
        --set-env-vars="MAIN_CLASS=com.shahkaar.cloud_functions.functions.HttpFunctionSpringBoot"
        --project cloud-functions

  Deploy to Google Run Functions:
    gcloud functions deploy http-function-sb --gen2 \
        --project cloud-functions-448122 --region us-central1 --source=. --runtime=java21 \
        --entry-point=org.springframework.cloud.function.adapter.gcp.GcfJarLauncher \
        --trigger-http --allow-unauthenticated \
        --set-env-vars="spring_profile_active=local" \
        --set-env-vars="GOOGLE_FUNCTION_TARGET=apply" \
        --set-env-vars="MAIN_CLASS=com.shahkaar.cloud_functions.functions.HttpFunctionSpringBoot"

  Test Function:
    curl --header "Content-Type: application/json" \
        --request POST \
        --data '{"id" : "123","role" : "Captain","fName" : "Donald","lName" : "Duck"}' \
        https://us-central1-cloud-functions-448122.cloudfunctions.net/http-function-sb

  Delete function
    gcloud functions delete http-function-sb --region us-central1 --project cloud-functions-448122
    gcloud functions list --project cloud-functions-448122

 */