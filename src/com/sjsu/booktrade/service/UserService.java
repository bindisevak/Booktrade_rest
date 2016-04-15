package com.sjsu.booktrade.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;

import com.sjsu.booktrade.model.UserTO;
import com.sjsu.booktrade.util.ConnectionPool;

@Path("/user")
public class UserService {
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(UserTO userInfo) throws Exception{
		boolean registerStatus = false;
		
		if(userInfo.getFirstName().trim().length() > 0 && userInfo.getPassword().trim().length() > 0 && userInfo.getLastName().trim().length() > 0 && 
				userInfo.getEmailId().trim().length() > 0 &&
				userInfo.getContactNumber().trim().length() > 0){
		
			Connection connection = ConnectionPool.getConnectionFromPool();
			
			PreparedStatement preparedStatement = connection
					.prepareStatement("select user.userId from booktrade.user where userinfo.emailId = ?");
			
			preparedStatement.setString(1, userInfo.getEmailId());
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()){
				userInfo.setUserId(resultSet.getInt("userId"));
				return Response.status(500).entity(" User with the given email already registered "+userInfo.getFirstName()).build();
			}else{
				preparedStatement = connection
						.prepareStatement("insert into booktrade.userinfo values(null, ? ,? ,? ,? ,? ,? ,? )");
				preparedStatement.setString(1, userInfo.getFirstName());
				preparedStatement.setString(2, userInfo.getLastName());
				preparedStatement.setString(3, userInfo.getEmailId());
				preparedStatement.setString(4, userInfo.getPassword());
				if(userInfo.getEmailId().endsWith("edu")){
					preparedStatement.setString(5, "STUDENT");
				}else{
					preparedStatement.setString(5, "GENERAL");
				}
				
				preparedStatement.setString(6, userInfo.getContactNumber());
				
				int registerUpdate = preparedStatement.executeUpdate();
				
				if(registerUpdate==1){
					
					preparedStatement = connection.prepareStatement("select user.userId from booktrade.user where userinfo.emailId = ?");
					
					preparedStatement.setString(1, userInfo.getEmailId());
					resultSet = preparedStatement.executeQuery();
					
					if(resultSet.next()){
						userInfo.setUserId(resultSet.getInt("userId"));
						registerStatus = true;
					}
					
					if(registerStatus){
						return Response.status(201).entity(userInfo).build();
					}else{
						return Response.status(500).entity(" failed registering user please try again "+userInfo.getFirstName()).build();
					}
				}else{
					return Response.status(500).entity(" failed registering user please try again "+userInfo.getLastName()).build();
				}
			}
				
		}else{
			return Response.status(400).entity(" All fields should be entered, please try again").build();
		}
	}
	
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginStatus(String userLogin) throws Exception { 
		System.out.println("Reached Login Web Service");
		boolean loginStatus = false;

		JSONObject jsonObject = new JSONObject(userLogin);
		String username = jsonObject.getString("username");
		String password = jsonObject.getString("password");
		
		if(username.trim().length() > 0 && password.trim().length() > 0){
			Connection connection = ConnectionPool.getConnectionFromPool();
	
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from booktrade.user where user.emailId = ? AND user.password = ?");
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
	
			ResultSet resultSet = preparedStatement.executeQuery();
			
			UserTO userInfo = new UserTO();
			
			while(resultSet.next()) {
				userInfo.setFirstName(resultSet.getString("fname"));
				userInfo.setLastName(resultSet.getString("lname"));
				userInfo.setPassword(resultSet.getString("password"));
				userInfo.setUserType(resultSet.getString("type"));
				userInfo.setContactNumber(resultSet.getString("contact_number"));
				userInfo.setEmailId(resultSet.getString("emailId"));
				userInfo.setUserId(resultSet.getInt("userId"));
				loginStatus = true;
			}
			
			if (loginStatus) {
				 return Response.status(200).entity(userInfo).build();					
			} else {
				return Response.status(401).entity("Login Failed for User").build();
			}
		}else{
			return Response.status(400).entity(" All fields should be entered, please try again").build();
		}
	}

	@POST
	@Path("/changePassword")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePassword(String userLogin) throws Exception { 
		

		JSONObject jsonObject = new JSONObject(userLogin);
		int userId = jsonObject.getInt("userId");
		String newPassword = jsonObject.getString("newpassword");
		
		if(userId > 0 && newPassword.trim().length() > 0){
			Connection connection = ConnectionPool.getConnectionFromPool();
	
			PreparedStatement preparedStatement = connection
					.prepareStatement("UPDATE booktrade.user SET user.password=? WHERE user.userId = ?");
			preparedStatement.setString(1, newPassword);
			preparedStatement.setInt(2, userId);
	
			preparedStatement.executeUpdate();
			
			return Response.status(201).entity(userId).build();
			
		}else{
			return Response.status(400).entity(" All fields should be entered, please try again ").build();
		}
	}
	
	@GET
	@Path("/{parameter}")
	public Response responseMsg(@PathParam("parameter") String parameter,
			@DefaultValue("Nothing to say") @QueryParam("value") String value) {
			System.out.println("Reached web service");
		String output = "Hello from: " + parameter + " : " + value;

		return Response.status(200).entity(output).build();
	}
	
	public static UserTO getUserDetailsFromId(int userId){
		
		try{
		UserTO userInfo = new UserTO();
		Connection connection = ConnectionPool.getConnectionFromPool();
		
		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from booktrade.user where user.userId = ?");
		preparedStatement.setInt(1, userId);

		ResultSet resultSet = preparedStatement.executeQuery();
		
		while(resultSet.next()) {
			userInfo.setFirstName(resultSet.getString("fname"));
			userInfo.setLastName(resultSet.getString("lname"));
			userInfo.setPassword(resultSet.getString("password"));
			userInfo.setUserType(resultSet.getString("type"));
			userInfo.setContactNumber(resultSet.getString("contact_number"));
			userInfo.setEmailId(resultSet.getString("emailId"));
			userInfo.setUserId(resultSet.getInt("userId"));
		}
		return userInfo;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}