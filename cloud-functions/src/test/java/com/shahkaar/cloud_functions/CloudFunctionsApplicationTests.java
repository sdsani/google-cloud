package com.shahkaar.cloud_functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.events.cloud.storage.v1.StorageObjectData;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.shahkaar.cloud_functions.converter.StorageObjectDataMessageConverter;
import com.shahkaar.cloud_functions.data.Employee;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.function.json.JacksonMapper;

import java.io.BufferedReader;

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

	@Test
	void jsonToJava() throws JsonProcessingException {
		String json = "{\"kind\":\"storage#object\"" +
						",\"id\":\"event-function-test/.gitignore/1737322979121115\",\"selfLink\":" +
						"\"https://www.googleapis.com/storage/v1/b/event-function-test/o/.gitignore\",\"name\":\".gitignore\"" +
						",\"bucket\":\"event-function-test\",\"generation\":\"1737322979121115\",\"metageneration\":\"1\"" +
						",\"contentType\":\"application/octet-stream\",\"timeCreated\":\"2025-01-19T21:42:59.124Z\"," +
						"\"updated\":\"2025-01-19T21:42:59.124Z\",\"storageClass\":\"STANDARD\",\"timeStorageClassUpdated\"" +
						":\"2025-01-19T21:42:59.124Z\",\"size\":\"12\",\"md5Hash\":\"8EqPJQUsNWLehU8/W7NvpA\\u003d\\u003d\"," +
						"\"mediaLink\":\"https://storage.googleapis.com/download/storage/v1/b/event-function-test/o/.gitignore?generation\\u003d1737322979121115\\u0026alt\\u003dmedia\"," +
						"\"crc32c\":\"2ZpuwQ\\u003d\\u003d\",\"etag\":\"CNuvn7jggosDEAE\\u003d\"}";

		log.info(json);
		//ObjectMapper objectMapper = new ObjectMapper();
		//StorageObjectData object = objectMapper.readValue(json, StorageObjectData.class);
		//log.info(object.toString());

		StorageObjectData.Builder builder = StorageObjectData.newBuilder();

		try {
			JsonFormat.parser().merge(json, builder);
		} catch (InvalidProtocolBufferException e) {
			throw new RuntimeException(e);
		}
		StorageObjectData data = builder.build();
		log.info("=========================================================================");
		log.info(data.toString());
		log.info("=========================================================================");
	}

}
