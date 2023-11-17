package controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Vector;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.PetListModel;

public class PetListController {
	@FXML
	private TableView<Pet> petTable;
	@FXML
    private TableColumn<Pet, String> nameColumn;
	@FXML
    private TableColumn<Pet, String> breedColumn;
	@FXML
    private TableColumn<Pet, Float> priceColumn;
	@FXML
    private TableColumn<Pet, Integer> ageColumn;
	private PetListModel model;
	
	private final ObservableList<Pet> obList = FXCollections.observableArrayList();

	public PetListController() {
		model = new PetListModel();
		System.out.print("petListController");
		getPetList();
	}
	@FXML
    private void initialize(){
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        breedColumn.setCellValueFactory(cellData -> cellData.getValue().breed);
        priceColumn.setCellValueFactory(new PropertyValueFactory<Pet, Float>("price"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<Pet, Integer>("age"));
		System.out.println("setItems");
        //绑定数据到TableView
        petTable.setItems(obList);

        //添加数据到personData，TableView会自动更新
//        obList.add(new Pet("name2", "male", new Float(200.10), 20));
        System.out.println("add new");
    }

	
	@SuppressWarnings("unchecked")
	public void getPetList() {
		Vector<Vector<Object>> data = model.queryPetList();
		System.out.println("getList: " + data);
		for (int i = 0; i < data.size(); i++) {
			String name = (String) data.get(i).get(1);
			String breed = (String) data.get(i).get(2);
			BigDecimal price = (BigDecimal) data.get(i).get(5);
			price.setScale(2, RoundingMode.HALF_UP);
			Float price2 = Float.parseFloat(price.toString());
			Integer age = (Integer) data.get(i).get(3);
			
//			ObservableList<Pet> obList = FXCollections.observableArrayList();
			obList.add(new Pet(name, breed, price2, age));
			System.out.println("name: " + data.get(i).get(5));
//			System.out.println("obList add" + name + ", " + breed + ", " + price + ", " + age);
		}
		System.out.print("obList" + obList);
	}
	
	
	public static class Pet {
		 
        private final SimpleStringProperty name;
        private final SimpleStringProperty breed;
        private final SimpleFloatProperty price;
        private final SimpleIntegerProperty age;
//        private final SimpleDateProperty birthday;
 
        private Pet(String name, String breed, Float price, Integer age) {
        	System.out.println("Pet()");
//        	this.setName(name);
//        	this.setAge(age);
            this.name = new SimpleStringProperty(name);
            this.breed = new SimpleStringProperty(breed);
            this.price = new SimpleFloatProperty(price);
            this.age = new SimpleIntegerProperty(age);
//            this.birthday = new SimpleObjectProperty<LocalDate>(birthday);
        }
 
        public String getName() {
            return name.get();
        }
 
        public void setName(String pName) {
            name.set(pName);
        }
 
        public String getBreed() {
            return breed.get();
        }
 
        public void setBreed(String pBreed) {
            breed.set(pBreed);
        }
 
        public Float getPrice() {
            return price.get();
        }
 
        public void setPrice(Float pPrice) {
            price.set(pPrice);
        }
        
        public Integer getAge() {
        	return age.get();
        }
        public void setAge(Integer pAge) {
        	age.set(pAge);
        }
    }
	
	
}


