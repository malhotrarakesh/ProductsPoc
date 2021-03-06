package com.serverless.products.functions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.serverless.products.model.Product;
import com.serverless.products.model.Response;
import com.serverless.products.utils.Utilities;

public class DeleteProduct implements RequestStreamHandler {

	private static final String BUCKET_NAME = "product-image-bucket";
	
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		String message = "Something wrong happened, please try again!!!";
		Connection connection = null;
		try {
			JsonParser parser = new JsonParser();
			JsonObject inputObj = null;
	        inputObj = parser.parse(IOUtils.toString(input)).getAsJsonObject();
	        
			String s = inputObj.get("body").getAsString();
			JsonElement e = parser.parse(s);
					
			Product product = Utilities.getGson().fromJson(e, Product.class);
			
			connection = Utilities.getConnnection();
			deleteProduct(connection, product);
			
			AmazonS3Client client = Utilities.getS3Client();
			client.deleteObject(BUCKET_NAME, product.getTitle() + ".jpg");
			
			message = "Product deleted successfully!!!";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Utilities.closeResources(connection, null);
		}
		
		try {
			Map<String, String> responseHeaders = new HashMap<String, String>();
			responseHeaders.put("Access-Control-Allow-Origin", "*");
			Response responseObj = new Response(message, responseHeaders, HttpStatus.SC_OK);
            IOUtils.write(Utilities.asJson(responseObj, Response.class), output);
        } catch (final IOException e) {
        	e.printStackTrace();
        } finally {
        	output.close();
        }
	}
	
	private void deleteProduct(Connection connection, Product product) throws Exception {
		PreparedStatement statement = connection.prepareStatement("DELETE FROM PRODUCTS WHERE ID=" + product.getId());
		statement.execute();
	}

}
