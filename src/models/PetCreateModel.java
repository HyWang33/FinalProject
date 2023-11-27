package models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class PetCreateModel extends DBConnect {
	@SuppressWarnings("rawtypes")
	public Boolean createPet(Map petMap) throws NoSuchAlgorithmException {
		if (queryExistName((String) petMap.get("name"))) {
			System.out.println("user exist");
			return false;
		}
		String SQL = "INSERT INTO Hongyang_pet_list(name, price, breed, age) " +
					"VALUES(?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
			String name = (String) petMap.get("name");
			Float price = (Float) petMap.get("price");
			String breed = (String) petMap.get("breed");
			Integer age = (Integer) petMap.get("age");
			pstmt.setString(1, name);
			pstmt.setFloat(2, price);
			pstmt.setString(3, breed);
			pstmt.setInt(4, age);
			Integer res = pstmt.executeUpdate();
			System.out.println("pet created");
			return res > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean queryExistName(String name) {
		ResultSet rs = null;
		String SQL = "SELECT * FROM hongyang_pet_list WHERE name = ?";
		try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
			stmt.setString(1, name);
			
			rs = stmt.executeQuery();
			System.out.println("console log user");
			if (rs.next()) {
				System.out.println("user rs" + rs.getInt("id"));
				return true;
			}
			System.out.println("user not defined");
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return false;
	}
	
	public Boolean updatePet(Map petMap) {
//		if (queryExistName((String) petMap.get("name"))) {
//			System.out.println("user exist");
//			return false;
//		}
		String SQL = "UPDATE Hongyang_pet_list SET name = ?, price = ?, breed = ?, age = ? WHERE (id = ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
			Integer id = (Integer) petMap.get("id");
			String name = (String) petMap.get("name");
			Float price = (Float) petMap.get("price");
			String breed = (String) petMap.get("breed");
			Integer age = (Integer) petMap.get("age");
			pstmt.setString(1, name);
			pstmt.setFloat(2, price);
			pstmt.setString(3, breed);
			pstmt.setInt(4, age);
			pstmt.setInt(5, id);
			Integer res = pstmt.executeUpdate();
			System.out.println("pet update: " + res);
			return res > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void updateBalance(Integer userId, Float balance) {
		String SQL = "UPDATE Hongyang_pet_user SET balance = ? WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
			stmt.setFloat(1, balance);
			stmt.setInt(2, userId);
			stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	public Boolean deletePet(Integer petId) {
		String SQL = "DELETE FROM Hongyang_pet_list WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
			pstmt.setInt(1, petId);
			Integer res = pstmt.executeUpdate();
			return res > 0;
		} catch (SQLException se) {
			System.out.println("update error");
			se.printStackTrace();
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
