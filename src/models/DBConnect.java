package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
	static final String DB_URL = "jdbc:mysql://www.papademas.net:3307/510fp?tinyInt1isBit=false&autoReconnect=true&useSSL=false";
	static final String USER = "fp510", PASSWORD = "510";
	
	protected static Connection connection;

	public Connection getConnection() {
		return connection;
	}
	
	public DBConnect() {
		try {
			connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		} catch (SQLException e) {
			System.out.println("Error creating connection to database: " + e);
			System.exit(-1);
		}
	}
	
//	public Connection connect() throws SQLException {
//		return DriverManager.getConnection(DB_URL, USER, PASS);
//	}
}
