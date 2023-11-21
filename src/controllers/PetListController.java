package controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Vector;

import application.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
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
	@FXML
	private TableColumn<Pet, Boolean> isSaledColumn;
	@FXML
	private TableColumn<Pet, Boolean> operateColumn;
	@FXML
	private TableColumn<Pet, Integer> numberColumn;
	
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
        operateColumn.setCellFactory(createButtonCellFactory());
        isSaledColumn.setCellValueFactory(new PropertyValueFactory<Pet, Boolean>("isSaled"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<Pet, Integer>("number"));
        
		System.out.println("setItems");
        //绑定数据到TableView
        petTable.setItems(obList);

        //添加数据到personData，TableView会自动更新
//        obList.add(new Pet("name2", "male", new Float(200.10), 20));
        System.out.println("add new");
    }

	// TableColoumn add Button
	private Callback<TableColumn<Pet, Boolean>, TableCell<Pet, Boolean>> createButtonCellFactory() {
        return new Callback<TableColumn<Pet, Boolean>, TableCell<Pet, Boolean>>() {
            @Override
            public TableCell<Pet, Boolean> call(final TableColumn<Pet, Boolean> param) {
                return new TableCell<Pet, Boolean>() {
                    private final Button btn = new Button("Buy me");

                    {
                        // 设置按钮点击事件
                        btn.setOnAction(event -> {
                            // 获取当前行的数据对象
                            Pet pet = getTableView().getItems().get(getIndex());
                            System.out.println("Button clicked for: " + pet.getName() + " " + pet.getIsSaled());
                            puchasePet();
                        });
                    }

                    // 渲染单元格
                    @Override
                    public void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
    }
	
	public void puchasePet() {
		try {
			AnchorPane root;
			root = (AnchorPane) FXMLLoader.load(getClass().getResource("/views/OrderView.fxml"));
			Main.stage.setTitle("Order List View");
			Scene scene = new Scene(root);
			Main.stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void getPetList() {
		Vector<Vector<Object>> data = model.queryPetList();
		System.out.println("getList: " + data);
		for (int i = 0; i < data.size(); i++) {
			String name = (String) data.get(i).get(1);
			String breed = (String) data.get(i).get(2);
			Float price = (Float) data.get(i).get(5);
			Integer age = (Integer) data.get(i).get(3);
			Boolean isSaled = (Boolean) data.get(i).get(6);
			
//			ObservableList<Pet> obList = FXCollections.observableArrayList();
			obList.add(new Pet(i + 1, name, breed, price, age, isSaled));
			System.out.println("name: " + data.get(i).get(5) + ", isSaled: " + isSaled);
//			System.out.println("obList add" + name + ", " + breed + ", " + price + ", " + age);
		}
		System.out.print("obList" + obList);
	}
	
	
	public static class Pet {
		private final SimpleIntegerProperty number;
        private final SimpleStringProperty name;
        private final SimpleStringProperty breed;
        private final SimpleFloatProperty price;
        private final SimpleIntegerProperty age;
        private final SimpleBooleanProperty isSaled;
//        private final SimpleDateProperty birthday;
 
        private Pet(Integer number, String name, String breed, Float price, Integer age, Boolean isSaled) {
        	System.out.println("Pet()");
//        	this.setName(name);
//        	this.setAge(age);
        	this.number = new SimpleIntegerProperty(number);
            this.name = new SimpleStringProperty(name);
            this.breed = new SimpleStringProperty(breed);
            this.price = new SimpleFloatProperty(price);
            this.age = new SimpleIntegerProperty(age);
            this.isSaled = new SimpleBooleanProperty(isSaled);
//            this.birthday = new SimpleObjectProperty<LocalDate>(birthday);
        }
        
        public Integer getNumber() {
        	return number.get();
        }
        public void setNumber(Integer pNumber) {
        	age.set(pNumber);
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
        
        public Boolean getIsSaled() {
        	return isSaled.get();
        }
        public void setIsSale(Boolean pIsSaled) {
        	isSaled.set(pIsSaled);
        }
    }
	
	
}


