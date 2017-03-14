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

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.serverless.products.Utilities.Utilities;
import com.serverless.products.datasource.DatasourceUtils;
import com.serverless.products.model.Product;
import com.serverless.products.model.Response;

public class GetProducts implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		Connection connection = null;
		ResultSet resultSet = null;
		List<Product> products = new ArrayList<Product>();
		try {
			connection = DatasourceUtils.getConnnection();
			products = getProducts(connection);
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			DatasourceUtils.closeResources(connection, resultSet);
		}
		
		try {
			Response responseObj = new Response(Utilities.asJson(products, List.class), new HashMap<String, String>(), HttpStatus.SC_OK);
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
			
			products.add(product);
		}
		
		return products;
	}
	
	
}
