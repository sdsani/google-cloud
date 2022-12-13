package com.shahkaar.gcpbigquerybatch;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.cloud.bigquery.BigQuery;
import com.shahkaar.gcpbigquerybatch.bigquery.BigQueryClient;
import com.shahkaar.gcpbigquerybatch.bigquery.BigqueryServiceFactory;
import com.shahkaar.gcpbigquerybatch.model.BankLocation;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class GcpBigqueryBatchApplication {

	private static final String QUERY = " SELECT fdic_certificate_number, institution_name, branch_name, branch_number, main_office, branch_address, "
				+ "branch_city, zip_code, branch_county, county_fips_code, state, state_name "  
				+ "FROM `bigquery-public-data.fdic_banks.locations` LIMIT 5";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(GcpBigqueryBatchApplication.class, args);
		readData();
	}

	private static void readData() throws IOException, InterruptedException {
		BigQuery service = BigqueryServiceFactory.getService();		
		BigQueryClient client = new BigQueryClient(service, true);
		List<BankLocation> bankLocations = client.query(QUERY, BankLocation.class);
		for (BankLocation repo : bankLocations) {
			log.info(repo.toString());
		}
	}
	
}
