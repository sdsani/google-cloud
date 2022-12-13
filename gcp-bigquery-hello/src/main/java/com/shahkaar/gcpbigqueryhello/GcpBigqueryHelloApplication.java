package com.shahkaar.gcpbigqueryhello;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.cloud.bigquery.BigQuery;
import com.shahkaar.gcpbigqueryhello.bigquery.BigQueryClient;
import com.shahkaar.gcpbigqueryhello.bigquery.BigqueryServiceFactory;
import com.shahkaar.gcpbigqueryhello.model.BankLocation;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class GcpBigqueryHelloApplication {

	private static final String QUERY = " SELECT fdic_certificate_number, institution_name, branch_name, branch_number, main_office, branch_address, "
			  							+ "branch_city, zip_code, branch_county, county_fips_code, state, state_name "  
			  							+ "FROM `bigquery-public-data.fdic_banks.locations` LIMIT 5";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(GcpBigqueryHelloApplication.class, args);
		readData();
	}

	private static void readData() throws IOException, InterruptedException {
		BigQuery service = BigqueryServiceFactory.getService();		
		BigQueryClient client = new BigQueryClient(service);
		List<BankLocation> query2 = client.query(QUERY, BankLocation.class);
		for (BankLocation repo : query2) {
			log.info(repo.toString());
		}
	}
}
