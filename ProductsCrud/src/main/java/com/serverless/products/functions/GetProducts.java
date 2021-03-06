package com.serverless.products.functions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.serverless.products.model.Product;
import com.serverless.products.model.Response;
import com.serverless.products.utils.Utilities;

public class GetProducts implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		Connection connection = null;
		ResultSet resultSet = null;
		List<Product> products = new ArrayList<Product>();
		try {
			connection = Utilities.getConnnection();
			products = getProducts(connection);
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			Utilities.closeResources(connection, resultSet);
		}
		
		try {
			Map<String, String> responseHeaders = new HashMap<String, String>();
			responseHeaders.put("Access-Control-Allow-Origin", "*");
			Response responseObj = new Response(Utilities.asJson(products, List.class), responseHeaders, HttpStatus.SC_OK);
            IOUtils.write(Utilities.asJson(responseObj, Response.class), output);
        } catch (final IOException e) {
        	e.printStackTrace();
        } finally {
        	output.close();
        }
	}
	
	private List<Product> getProducts(Connection connection) throws Exception {
		
		ArrayList<Product> products = new ArrayList<Product>();
		
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM PRODUCTS");
		ResultSet rs = statement.executeQuery();
		
		while(rs.next()) {
			Product product = new Product();
			product.setId(rs.getInt("ID"));
			product.setTitle(rs.getString("TITLE"));
			product.setDescription(rs.getString("DESCRIPTION"));
			product.setImageSource(rs.getString("IMAGE_SRC"));
			
			products.add(product);
		}
		
		return products;
	}
	
	
}
