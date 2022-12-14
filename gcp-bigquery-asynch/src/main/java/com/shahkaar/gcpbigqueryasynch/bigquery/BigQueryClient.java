package com.shahkaar.gcpbigqueryasynch.bigquery;

import java.util.List;

public interface BigQueryClient {
	
	<T> List<T> query(String sql, Class<T> valueType) throws InterruptedException;
	<T> List<T> queryAsynch(String sql, Class<T> valueType) throws InterruptedException;
	
}
