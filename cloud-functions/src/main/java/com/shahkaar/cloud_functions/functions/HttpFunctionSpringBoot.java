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
        log.info("Data received: {}", employee.toString());
        log.info("Spring boot Variable: {}", sbEnv);
        return employee;
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
 */