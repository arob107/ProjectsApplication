package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import projects.exception.DbException;

public class DbConnection {

	private static String HOST = "localhost";
	private static String PASSWORD = "projects";
	private static int PORT = 3306;
	private static String SCHEMA = "projects";
	private static String USER = "projects";
	
	public static Connection getConnection() {
		// create a string variable named URI that contains the MySQL connection URI 
		String uri = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=false", HOST, PORT, 
				SCHEMA, USER, PASSWORD);
		
		// call DriverManager to obtain a connection. Pass the connection string (URL) to DriverManager.getConnection()
		// surround the call to DriverManager.getConnection() with a try/catch block. The catch block should catch
		// SQLException
		try {
			Connection conn = DriverManager.getConnection(uri);
			// print a message to the console if the connection is successful
			System.out.println("Connection Successful!");
			return conn;
		} catch (SQLException e) {
			// print an error message to the console if the connection fails. Throw a DbException if the connection fails
			System.out.println("Connection failed");
			throw new DbException(e);
		}
	}
}
