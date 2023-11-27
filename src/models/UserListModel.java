package models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class UserListModel extends DBConnect {
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
	public Vector<Map> queryUserList() {
		ResultSet rs = null;
		String SQL = "SELECT * FROM Hongyang_pet_user WHERE role = 1 OR role = 2";
		Vector<Map> data = new Vector<Map>();
		Vector<String> column = new Vector<String>();
		try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
			
			rs = stmt.executeQuery();
			System.out.println("console log user");
			
			ResultSetMetaData metaData = rs.getMetaData();
			int columnNum = metaData.getColumnCount();
			
			String cols = "";
			for (int i = 1; i <= columnNum; i++) {
				cols = metaData.getColumnName(i);
				column.add(cols);
			}
			System.out.println("columnNum" + columnNum);
			while (rs.next()) {
				Map petMap = new HashMap();
				Vector<Object> row = new Vector<Object>(columnNum);
				
				for (int i = 1; i <= columnNum; i++) {
					petMap.put(column.get(i - 1), rs.getObject(i));
//					row.addElement(i == 7 ? rs.getBoolean("isSaled") : rs.getObject(i));
				}
				data.addElement(petMap);
			}
			
			
			
			System.out.println("return data" + data);
			return data;
			
			
			
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return data;
	}
	
	public static boolean updateBalance(Integer userId, Float balance) {
		String SQL = "UPDATE Hongyang_pet_user SET balance = ? WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
			stmt.setFloat(1, balance);
			stmt.setInt(2, userId);
			Integer res = stmt.executeUpdate();
			return res > 0;
		} catch (SQLException se) {
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
