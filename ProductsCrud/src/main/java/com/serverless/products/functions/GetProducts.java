package com.serverless.products.functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.products.Utilities.Utilities;
import com.serverless.products.datasource.DatasourceUtils;
import com.serverless.products.model.AddProductRequest;
import com.serverless.products.model.Product;
import com.serverless.products.model.Response;

public class GetProducts implements RequestHandler<AddProductRequest, Response> {

	@Override
	public Response handleRequest(AddProductRequest input, Context context) {
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
		
		return new Response(Utilities.asJson(products, List.class), new HashMap<String, String>(), HttpStatus.SC_OK);
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
