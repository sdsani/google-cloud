package com.shahkaar.cloud_functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.shahkaar.cloud_functions.data.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class CloudFunctionsApplicationTests {

	@Test
	void contextLoads() throws JsonProcessingException {

		Employee employee = Employee.builder().id("123").fName("Donald").lName("Duck").role("Captain").build();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(employee);
		log.info(json);
	}

}
