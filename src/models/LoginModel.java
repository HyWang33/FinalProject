package models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
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
						" balance FLOAT(10, 2) NOT NULL DEFAULT 0.00, " + 
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map queryUser(String username, String password, Integer role) throws NoSuchAlgorithmException {
		ResultSet rs = null;
		boolean isValid = false;
		Map userMap = new HashMap();
		userMap.put("isValid", isValid);
		String SQL = "SELECT * FROM Hongyang_pet_user WHERE username = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
			pstmt.setString(1, username);
			pstmt.setInt(2, role);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Integer id = rs.getInt("id");
				String realPassword = rs.getString("password");
				String gender = rs.getString("gender");
				String email = rs.getString("email");
				Date birthday = rs.getDate("birthday");
				Float balance = rs.getFloat("balance");
				userMap.put("id", id);
				userMap.put("username", username);
				userMap.put("password", password);
				userMap.put("gender", gender);
				userMap.put("email", email);
				userMap.put("birthday", birthday);
				userMap.put("balance", balance);
				userMap.put("role", role);
				isValid = toHash(password).equals(realPassword);
				userMap.put("isValid", isValid);
				return userMap;
			}
			System.out.println("user not defined");
			
		} catch (SQLException se) {
			System.out.println("Error communication: " + se);
		}
		return userMap;
	}
	
	public String toHash(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(password.getBytes());
		byte byteData[] = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}
