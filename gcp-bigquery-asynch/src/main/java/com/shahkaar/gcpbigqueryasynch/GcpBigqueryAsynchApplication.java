package com.shahkaar.gcpbigqueryasynch;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.shahkaar.gcpbigqueryasynch.bigquery.BigQueryClient;
import com.shahkaar.gcpbigqueryasynch.bigquery.BigQueryClientImpl;
import com.shahkaar.gcpbigqueryasynch.bigquery.BigqueryServiceFactory;
import com.shahkaar.gcpbigqueryasynch.model.BankLocation;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class GcpBigqueryAsynchApplication {

	private static final String QUERY = " SELECT fdic_certificate_number, institution_name, branch_name, branch_number, main_office, branch_address, "
										+ "branch_city, zip_code, branch_county, county_fips_code, state, state_name "  
										+ "FROM `bigquery-public-data.fdic_banks.locations` LIMIT 5";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(GcpBigqueryAsynchApplication.class, args);
		readData();
	}

	private static void readData() throws IOException, InterruptedException {
		BigQueryClient client = new BigQueryClientImpl(BigqueryServiceFactory.getService(), log);		
		for (BankLocation repo : client.queryAsynch(QUERY, BankLocation.class)) {
			log.info(repo.toString());
		}		
	}
	
}
