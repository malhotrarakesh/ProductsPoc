package com.serverless.products.functions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import sun.misc.BASE64Decoder;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.serverless.products.model.Product;
import com.serverless.products.model.Response;
import com.serverless.products.utils.Utilities;

public class AddProduct implements RequestStreamHandler {

	private static final String BUCKET_NAME = "product-image-bucket";

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		Connection connection = null;
		
		try {
			AmazonS3Client client = Utilities.getS3Client();
			client.createBucket(BUCKET_NAME);
			
			Product product = getProduct(input);

			uploadImageToS3(client, product);

			URL url = client.getUrl(BUCKET_NAME, product.getTitle() + ".jpg");
			System.out.println("URL = " + url);

			connection = Utilities.getConnnection();
			addProduct(connection, product, url.toString());

			Map<String, String> responseHeaders = new HashMap<String, String>();
			responseHeaders.put("Access-Control-Allow-Origin", "*");
			Response responseObj = new Response(url.toString(), responseHeaders, HttpStatus.SC_OK);
			IOUtils.write(Utilities.asJson(responseObj, Response.class), output);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			output.close();
			Utilities.closeResources(connection, null);
		}

	}

	private Product getProduct(InputStream input) throws IOException {
		JsonParser parser = new JsonParser();
		JsonObject inputObj = parser.parse(IOUtils.toString(input)).getAsJsonObject();

		String s = inputObj.get("body").getAsString();
		JsonElement e = parser.parse(s);

		Product product = Utilities.getGson().fromJson(e, Product.class);
		return product;
	}

	private void uploadImageToS3(AmazonS3Client client, Product product) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		String imageSourceData = product.getImageSource().substring(product.getImageSource().indexOf(",", 1) + 1);

		byte[] imageByte = decoder.decodeBuffer(imageSourceData);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		bis.close();

		ObjectMetadata metaData = new ObjectMetadata();
		metaData.setContentLength(imageByte.length);

		client.putObject(BUCKET_NAME, product.getTitle() + ".jpg", bis, metaData);
	}
	
	private void addProduct(Connection connection, Product product, String url) throws Exception {
		PreparedStatement statement = connection.prepareStatement("SELECT MAX(ID) as ID FROM PRODUCTS");
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			int maxId = rs.getInt("ID");
			int nextId = ++maxId;
			String title = product.getTitle();
			String desc = product.getDescription();
			statement = connection.prepareStatement("INSERT INTO PRODUCTS (ID, TITLE, DESCRIPTION, IMAGE_SRC) VALUES (" + nextId + ",'" + title + "','" + desc + "','" + url + "')");
			statement.execute();
		}

	}

}
