package com.shahkaar.gcpbigqueryhello.bigquery;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

public class BigqueryServiceFactory {

	private static final String JSON_PATH = "./donotcommit/bg-service-account.json";
	private static BigQuery service = null;
    private static final Object SERVICE_LOCK = new Object();

    private BigqueryServiceFactory() {
    }

    public static BigQuery getService() throws IOException {
           synchronized (SERVICE_LOCK) {
                  if (service == null) {
                        service = createAuthorizedClient();
                  }
           }
           return service;
    }

	private static BigQuery createAuthorizedClient() throws IOException {
		
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(JSON_PATH));
		return BigQueryOptions.newBuilder()
									.setCredentials(credentials)
									.setProjectId("my-first-project-326102")
									.build().getService();
	}
}
