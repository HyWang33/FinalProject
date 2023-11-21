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
						" uid INTEGER not NULL, " +
						" shopId INTEGER not NULL, " +
						" name VARCHAR(45), " +
						" breed VARCHAR(45), " +
						" age INTEGER, " + 
						" birthday Date, " +
						" price FLOAT(10, 2) not NULL DEFAULT 0.00," +
						" isSaled Boolean, " +
						" PRIMARY KEY ( id ), " +
						"CONSTRAINT FK_User FOREIGN KEY (uid) REFERENCES Hongyang_pet_user (id), " +
						"CONSTRAINT FK_Shop FOREIGN KEY (shopId) REFERENCES Hongyang_pet_list (id))";
			stmt.executeUpdate(SQL);
			System.out.println("Created table in given database...");
			connection.close();
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	public Vector<Map> queryPetList() {
		ResultSet rs = null;
		String SQL = "SELECT * FROM Hongyang_pet_order";
		Vector<Map> data = new Vector<>();
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
				Vector<Object> row = new Vector<Object>(columnNum);
				
				Map orderMap = new HashMap<>();
				orderMap.put("name", rs.getObject("name"));
				
				orderMap.put("breed", rs.getObject("breed"));
				orderMap.put("age", rs.getObject("age"));
				orderMap.put("birthday", rs.getObject("birthday"));
				orderMap.put("isSaled", rs.getObject("isSaled"));
				orderMap.put("price", rs.getObject("price"));
				orderMap.put("buyer", rs.getObject("buyer"));
				data.addElement(orderMap);
			}
			
			
			
			connection.close();
			System.out.println("return data" + data);
			return data;
			
			
			
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return data;
	}

}