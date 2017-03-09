package com.serverless.products.model;

import java.util.Map;

public class Response {
	private final String body;
	private final Map<String, String> headers;
	private final int statusCode;
	
	public Response(String body, Map<String, String> headers, int statusCode) {
		this.body = body;
		this.headers = headers;
		this.statusCode = statusCode;
	}

	public String getBody() {
		return body;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public int getStatusCode() {
		return statusCode;
	}

}
