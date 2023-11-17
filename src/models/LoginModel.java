package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;


public class LoginModel extends DBConnect {
	Statement stmt = null;
	
	public void createTable() {
		try {
			System.out.println("Connecting to database to create Table...");
			System.out.println("Connected database successfully...");
			System.out.println("Creating table in given database...");
			
			stmt = connection.createStatement();
			
			String SQL = "CREATE TABLE IF NOT EXISTS Hongyang_pet_user " +
						"(id INTEGER not NULL AUTO_INCREMENT, " +
						" age INTEGER, " + 
						" gender VARCHAR(6), " +
						" birthday Date, " + 
						" balance numeric(8, 2), " + 
						" username VARCHAR(45), " +
						" password VARCHAR(45), " +
						" PRIMARY KEY ( id ))";
			stmt.executeUpdate(SQL);
			System.out.println("Created table in given database...");
			connection.close();
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	public Boolean queryUser(String username, String password) {
		ResultSet rs = null;
		String SQL = "SELECT * FROM Hongyang_pet_user WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
			stmt.setString(1, username);
			
			rs = stmt.executeQuery();
			System.out.println("console log user");
			if (rs.next()) {
				String realPassword = rs.getString("password");
				System.out.println("age: " + rs.getInt("age"));
				System.out.println("password: " + realPassword + ", my password: " + password);
//				connection.close();
				return password.equals(realPassword);
			}
			System.out.println("user not defined");
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return false;
	}
}
