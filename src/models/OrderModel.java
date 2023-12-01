package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class OrderModel extends DBConnect {
	Statement stmt = null;
	
	public void createTable() {
		try {
			System.out.println("Connecting to database to create Table...");
			System.out.println("Connected database successfully...");
			System.out.println("Creating table in given database...");
			
			stmt = connection.createStatement();
			
			String SQL = "CREATE TABLE IF NOT EXISTS Hongyang_pet_order " +
						"(id INTEGER not NULL AUTO_INCREMENT, " +
						" userId INTEGER not NULL, " +
						" petId INTEGER not NULL, " +
						" name VARCHAR(45), " +
						" breed VARCHAR(45), " +
						" age INTEGER, " + 
						" birthday Date, " +
						" price FLOAT(10, 2) not NULL DEFAULT 0.00," +
						" isSaled Boolean, " +
						" PRIMARY KEY ( id ), " +
						"CONSTRAINT FK_User FOREIGN KEY (userId) REFERENCES Hongyang_pet_user (id), " +
						"CONSTRAINT FK_Shop FOREIGN KEY (petId) REFERENCES Hongyang_pet_list (id))";
			stmt.executeUpdate(SQL);
			System.out.println("Created table in given database...");
			connection.close();
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector<Map> queryOrderList(Map userMap) {
		ResultSet rs = null;
		String SQL = "SELECT * FROM Hongyang_pet_order";
		Integer role = (Integer) userMap.get("role");
		if (role == 1) {
			SQL += " WHERE userId = ?";
		}
		Vector<Map> data = new Vector<>();
		Vector<String> column = new Vector<String>();
		try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
			if (role == 1) {
				Integer userId = (Integer) userMap.get("id");
				pstmt.setInt(1, userId);
			}
			rs = pstmt.executeQuery();
			
			ResultSetMetaData metaData = rs.getMetaData();
			int columnNum = metaData.getColumnCount();
			
			String cols = "";
			for (int i = 1; i <= columnNum; i++) {
				cols = metaData.getColumnName(i);
				column.add(cols);
			}
			while (rs.next()) {
				Vector<Object> row = new Vector<Object>(columnNum);
				
				Map orderMap = new HashMap<>();
				orderMap.put("name", rs.getObject("name"));
				
				orderMap.put("breed", rs.getObject("breed"));
				orderMap.put("age", rs.getObject("age"));
//				orderMap.put("birthday", rs.getObject("birthday"));
//				orderMap.put("isSaled", rs.getObject("isSaled"));
				orderMap.put("price", rs.getObject("price"));
				orderMap.put("buyer", rs.getObject("buyer"));
				data.addElement(orderMap);
			}
			
			
			
//			connection.close();
			return data;
			
			
			
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return data;
	}
	
	@SuppressWarnings("rawtypes")
	public static void createOrder(Map orderInfo) {
		String SQL = "INSERT INTO Hongyang_pet_order(userId, petId, name, breed, age, price, buyer) " +
				"VALUES(?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
			Integer userId = (Integer) orderInfo.get("userId");
			Integer petId = (Integer) orderInfo.get("petId");
			String buyer = (String) orderInfo.get("buyer");
			String name = (String) orderInfo.get("name");
			String breed = (String) orderInfo.get("breed");
			Integer age = (Integer) orderInfo.get("age");
			Float price = (Float) orderInfo.get("price");
			System.out.println("userId:" + userId);
			pstmt.setInt(1, userId);
			pstmt.setInt(2, petId);
			pstmt.setString(3, name);
			pstmt.setString(4, breed);
			pstmt.setInt(5, age);
			pstmt.setFloat(6, price);
			pstmt.setString(7, buyer);
			pstmt.executeUpdate();
			System.out.println("Order creaated!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}