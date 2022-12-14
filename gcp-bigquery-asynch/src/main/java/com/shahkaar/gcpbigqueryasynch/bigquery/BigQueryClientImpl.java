package com.shahkaar.gcpbigqueryasynch.bigquery;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.JobStatus;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;

public class BigQueryClientImpl implements BigQueryClient {

	private final BigQuery bigQuery;
    private final ObjectMapper objectMapper;
    private final boolean isBatch;
    private Logger log;

    public BigQueryClientImpl(BigQuery bigQuery, Logger log) {
    	this(bigQuery, false, log);
    }
    
	public BigQueryClientImpl(BigQuery bigQuery, boolean isBatch, Logger log) {
		this.isBatch = isBatch; 
		this.bigQuery = bigQuery;
		this.log = log;
		this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
	}

	@Override
	public <T> List<T> query(String sql, Class<T> valueType) throws InterruptedException {
		return readValues(query(sql), valueType);
	}
	
	@Override
	public <T> List<T> queryAsynch(String sql, Class<T> valueType) throws InterruptedException {
		
		Job job = buildJob(sql);
		do {
			log.info("Going to sleep until next check. Current state : {}, JobID : {} ", job.getStatus().getState(), job.getGeneratedId());
			TimeUnit.SECONDS.sleep(7);
		} while (!job.getStatus().getState().equals(JobStatus.State.DONE));
		
		log.info("Job is done, it is time to consume the results");
		return readValues(getQueryResults(job), valueType);
	}

    private TableResult query(String sql) throws InterruptedException {    	
        Job queryJob = buildJob(sql);
        queryJob = queryJob.waitFor();
        return getQueryResults(queryJob);
    }
    
	private Job buildJob(String sql) {
		
		QueryJobConfiguration queryJobConfiguration = buildQueryJobConfiguration(sql);
        JobId jobId = JobId.of(UUID.randomUUID().toString());
        return bigQuery.create(JobInfo.newBuilder(queryJobConfiguration)            	
        						.setJobId(jobId)
        						.build()
        						);
	}
	
	private TableResult getQueryResults(Job queryJob) throws JobException, InterruptedException {
		if (queryJob == null) {
            throw new IllegalStateException("Job no longer exists");
        } else if (queryJob.getStatus().getError() != null) {
            throw new IllegalStateException(queryJob.getStatus().getError().toString());
        }
        return queryJob.getQueryResults(BigQuery.QueryResultsOption.pageSize(100));
	}
    
	private QueryJobConfiguration buildQueryJobConfiguration(String sql) {
		return QueryJobConfiguration
				.newBuilder(sql)
				.setPriority(isBatch 
								? QueryJobConfiguration.Priority.BATCH 
								: QueryJobConfiguration.Priority.INTERACTIVE)
				.build();
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

    private Map<String, Object> getProperties(FieldValueList row, List<String> fieldNames) {       
       return fieldNames.stream()
                    .collect(
                         Collectors.toMap(fieldName -> fieldName, fieldName -> row.get(fieldName).getValue())
                    );
    }
}
