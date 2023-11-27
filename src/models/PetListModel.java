package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class PetListModel extends DBConnect {
	DBConnect conn = null;
	Statement stmt = null;
	
	public PetListModel() {
		conn = new DBConnect();
	}
	
	public void createTable() {
		try {
			System.out.println("Connecting to database to create Table...");
			System.out.println("Connected database successfully...");
			System.out.println("Creating table in given database...");
			
			stmt = connection.createStatement();
			
			String SQL = "CREATE TABLE IF NOT EXISTS Hongyang_pet_list " +
						"(id INTEGER not NULL AUTO_INCREMENT, " +
						" name VARCHAR(45), " +
						" breed VARCHAR(45), " +
						" age INTEGER, " + 
						" birthday DATE, " +
						" price FLOAT(10, 2) not NULL DEFAULT 0.00," +
						" isSaled Boolean, " +
						" PRIMARY KEY ( id ))";
			stmt.executeUpdate(SQL);
			System.out.println("Created table in given database...");
//			connection.close();
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector<Map> queryPetList() {
		ResultSet rs = null;
		String SQL = "SELECT * FROM Hongyang_pet_list";
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
					petMap.put(column.get(i - 1), column.get(i - 1).equals("isSaled") ? rs.getBoolean("isSaled") : rs.getObject(i));
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
	
	@SuppressWarnings("unchecked")
	public void queryCreateOrder(Map petInfo) {
		Integer petId = (Integer) petInfo.get("petId");
		Integer userId = (Integer) petInfo.get("userId");
		Float price = (Float) petInfo.get("price");
		Float balance = (Float) petInfo.get("balance");
		Float newBalance = balance - price;
		System.out.println("update tinyint petId:" + petId);
		String SQL = "UPDATE Hongyang_pet_list SET isSaled = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
			pstmt.setInt(1, 1);
			pstmt.setInt(2, petId);
			pstmt.executeUpdate();
		} catch (SQLException se) {
			System.out.println("update error");
			se.printStackTrace();
		}
		OrderModel.createOrder(petInfo);
		SignUpModel.updateBalance(userId, newBalance);
	}
	
}


