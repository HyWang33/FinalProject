package controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import application.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ReadOnlyProperty;
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
import javafx.stage.Stage;
import javafx.util.Callback;
import models.OrderModel;
import models.PetListModel;

public class OrderController {
	@FXML
	private TableView<Pet> petOrderTable;
	@FXML
    private TableColumn<Pet, String> nameColumn;
	@FXML
    private TableColumn<Pet, String> breedColumn;
	@FXML
    private TableColumn<Pet, Float> priceColumn;
	@FXML
    private TableColumn<Pet, Integer> ageColumn;
	@FXML
	private TableColumn<Pet, String> buyerColumn;
//	@FXML
//	private TableColumn<Pet, Boolean> operateColumn;
	@FXML
	private TableColumn<Pet, Integer> numberColumn;
	
	private OrderModel model;
	private Stage stage;
	private Stage orderStage;
	private Map userMap;
	private Thread t1;
	
	private final ObservableList<Pet> obList = FXCollections.observableArrayList();

	public OrderController() {
		t1 = new Thread(() -> {
			model = new OrderModel();
			System.out.print("petListController");
			getPetList();
		});
		t1.start();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setOrderStage(Stage stage) {
		this.orderStage = stage;
	}
	
	public void setUser(Map userMap) {
		this.userMap = userMap;
	}
	
	@FXML
    private void initialize(){
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        breedColumn.setCellValueFactory(cellData -> cellData.getValue().breed);
        priceColumn.setCellValueFactory(new PropertyValueFactory<Pet, Float>("price"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<Pet, Integer>("age"));
//        operateColumn.setCellFactory(createButtonCellFactory());
        buyerColumn.setCellValueFactory(cellData -> cellData.getValue().buyer);
        numberColumn.setCellValueFactory(new PropertyValueFactory<Pet, Integer>("number"));
        
		System.out.println("setItems");
        //绑定数据到TableView
        petOrderTable.setItems(obList);
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
                            System.out.println("Button clicked for: " + pet.getName() + " " + pet.getBuyer());
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
	
	public void onBack() {
		t1.interrupt();
		try {
			orderStage.close();
			stage.show();
			
//			AnchorPane root;
//			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PetListView.fxml"));
//			root = (AnchorPane) loader.load();
//			stage.setTitle("Pet List View");
//			PetListController petListController = loader.getController();
//			petListController.setStage(stage);
//			petListController.setUser(userMap);
//			Scene scene = new Scene(root);
//			stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
	}
	
	public void puchasePet() {
		
	}
	
	@SuppressWarnings("unchecked")
	public void getPetList() {
		Vector<Map> data = model.queryPetList();
		System.out.println("getList: " + data);
		for (int i = 0; i < data.size(); i++) {
			Map orderMap = (Map) data.get(i);
			String name = (String) orderMap.get("name");
			String breed = (String) orderMap.get("breed");
			Float price = (Float) orderMap.get("price");
			Integer age = (Integer) orderMap.get("age");
			String buyer = (String) orderMap.get("buyer");
			
//			ObservableList<Pet> obList = FXCollections.observableArrayList();
			obList.add(new Pet(i + 1, name, breed, price, age, buyer));
			System.out.println("name: " + data.get(i).get(5) + ", buyer: " + buyer);
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
        private final SimpleStringProperty buyer;
//        private final SimpleDateProperty birthday;
 
        private Pet(Integer number, String name, String breed, Float price, Integer age, String buyer) {
        	System.out.println("Pet()");
//        	this.setName(name);
//        	this.setAge(age);
        	this.number = new SimpleIntegerProperty(number);
            this.name = new SimpleStringProperty(name);
            this.breed = new SimpleStringProperty(breed);
            this.price = new SimpleFloatProperty(price);
            this.age = new SimpleIntegerProperty(age);
            this.buyer = new SimpleStringProperty(buyer);
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
        
        public String getBuyer() {
        	return buyer.get();
        }
        public void setIsSale(String pBuyer) {
        	buyer.set(pBuyer);
        }
    }
	
	
}

