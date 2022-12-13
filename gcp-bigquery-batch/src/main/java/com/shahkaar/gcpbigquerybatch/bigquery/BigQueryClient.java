package com.shahkaar.gcpbigquerybatch.bigquery;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;

public class BigQueryClient {

	private final BigQuery bigQuery;
    private final ObjectMapper objectMapper;
    private final boolean isBatch;

	public BigQueryClient(BigQuery bigQuery) {
		this(bigQuery, false);
	}
	
	public BigQueryClient(BigQuery bigQuery, boolean isBatch) {
		this.isBatch = isBatch; 
		this.bigQuery = bigQuery;
		this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
	}

	public <T> List<T> query(String sql, Class<T> valueType) throws InterruptedException {
		return readValues(query(sql), valueType);
	}

    private TableResult query(String sql) throws InterruptedException {
    	
        QueryJobConfiguration queryJobConfiguration = QueryJobConfiguration
        													.newBuilder(sql)
        													.setPriority(isBatch 
        																	? QueryJobConfiguration.Priority.BATCH 
        																	: QueryJobConfiguration.Priority.INTERACTIVE)
        													.build();
        JobId jobId = JobId.of(UUID.randomUUID().toString());
        Job queryJob = bigQuery.create(
            JobInfo.newBuilder(queryJobConfiguration)            	
                .setJobId(jobId)
                .build()
        );

        queryJob = queryJob.waitFor();

        if (queryJob == null) {
            throw new IllegalStateException("Job no longer exists");
        } else if (queryJob.getStatus().getError() != null) {
            throw new IllegalStateException(queryJob.getStatus().getError().toString());
        }
        return queryJob.getQueryResults(BigQuery.QueryResultsOption.pageSize(100));
    }

    private <T> List<T> readValues(TableResult result, Class<T> valueType) {        
       List<String> fieldNames = getFieldNames(result);
       return StreamSupport.stream(result.iterateAll().spliterator(), true)
              .map( fvl -> objectMapper.convertValue(getProperties(fvl, fieldNames), valueType))
              .toList();
    }  

    private List<String> getFieldNames(TableResult tableResult) {
        return tableResult.getSchema().getFields()
            .stream()
            .map(Field::getName)
            .toList();
    }

    /*private static Map<String, Object> getProperties(FieldValueList row, List<String> fieldNames) {
        Map<String, Object> properties = new HashMap<>(fieldNames.size());
        for (String fieldName : fieldNames) {
            properties.put(fieldName, row.get(fieldName).getValue());
        }
        return properties;
    }*/
    
    private Map<String, Object> getProperties(FieldValueList row, List<String> fieldNames) {       
       return fieldNames.stream()
                    .collect(
                         Collectors.toMap(fieldName -> fieldName, fieldName -> row.get(fieldName).getValue())
                    );
    }
}
