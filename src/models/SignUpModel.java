package models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Map;

public class SignUpModel extends DBConnect {
	
	@SuppressWarnings("rawtypes")
	public Boolean createUser(Map userMap) throws NoSuchAlgorithmException {
		if (queryExistName((String) userMap.get("username"))) {
			System.out.println("user exist");
			return false;
		}
		String SQL = "INSERT INTO Hongyang_pet_user(username, password, gender, email, birthday, role) " +
					"VALUES(?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
			String username = (String) userMap.get("username");
			String password = (String) userMap.get("password");
			String gender = (String) userMap.get("gender");
			String email = (String) userMap.get("email");
			Date birthday = (Date) userMap.get("birthday");
			Integer role = (Integer) userMap.get("role");
			pstmt.setString(1, username);
			pstmt.setString(2, toHash(password));
			pstmt.setString(3, gender);
			pstmt.setString(4, email);
			pstmt.setDate(5, birthday);
			pstmt.setInt(6, role);
			Integer res = pstmt.executeUpdate();
			return res > 0;
		} catch (SQLException e) {
			System.out.println("Error communication: " + e);
		}
		return false;
	}
	
	public boolean queryExistName(String username) {
		ResultSet rs = null;
		String SQL = "SELECT * FROM hongyang_pet_user WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
			stmt.setString(1, username);
			
			rs = stmt.executeQuery();
			System.out.println("console log user");
			if (rs.next()) {
				System.out.println("user rs" + rs.getInt("id"));
				return true;
			}
			System.out.println("user not defined");
			
		} catch (SQLException se) {
			System.out.println("Error communication: " + se);
		}
		return false;
	}
	
	public static Boolean updateBalance(Integer userId, Float balance) {
		String SQL = "UPDATE Hongyang_pet_user SET balance = ? WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
			stmt.setFloat(1, balance);
			stmt.setInt(2, userId);
			Integer res = stmt.executeUpdate();
			return res > 0;
		} catch (SQLException se) {
			System.out.println("Error communication: " + se);
		}
		return false;
	}
	
	public Boolean updateUser(Map userMap) throws NoSuchAlgorithmException {
		String SQL = "UPDATE Hongyang_pet_user SET username = ?, password = ?, gender = ?, email = ?, birthday = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
			Integer id = (Integer) userMap.get("id");
			String username = (String) userMap.get("username");
			String password = (String) userMap.get("password");
			String gender = (String) userMap.get("gender");
			String email = (String) userMap.get("email");
			Date birthday = (Date) userMap.get("birthday");
			pstmt.setString(1, username);
			pstmt.setString(2, toHash(password));
			pstmt.setString(3, gender);
			pstmt.setString(4, email);
			pstmt.setDate(5, birthday);
			pstmt.setInt(6, id);
			Integer res = pstmt.executeUpdate();
			return res > 0;
		} catch (SQLException se) {
			System.out.println("Error communication: " + se);
		}
		return false;
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
