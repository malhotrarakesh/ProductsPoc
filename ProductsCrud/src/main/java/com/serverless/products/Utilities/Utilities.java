package com.serverless.products.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utilities {
	
	public static Gson getGson() {
		return new GsonBuilder().create();
	}
	
	public static <T> String asJson(Object source, Class<T> type) {
		return new GsonBuilder().create().toJson(source, type);
	}
}
