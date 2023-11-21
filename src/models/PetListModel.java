package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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
			connection.close();
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	public Vector<Vector<Object>> queryPetList() {
		ResultSet rs = null;
		String SQL = "SELECT * FROM Hongyang_pet_list";
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
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
				
				for (int i = 1; i <= columnNum; i++) {
					row.addElement(i == 7 ? rs.getBoolean("isSaled") : rs.getObject(i));
				}
				data.addElement(row);
			}
			
			
			
//			connection.close();
			System.out.println("return data" + data);
			return data;
			
			
			
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return data;
	}
}

class Pet {
	private String name;
	private String breed;
	private Integer age;
}
