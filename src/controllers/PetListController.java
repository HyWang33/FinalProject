package controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
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
	private TableColumn<Pet, Integer> idColumn;
	@FXML
	private Pane imagePane;
	@FXML
	private Label txtUsername;
	@FXML
	private Label txtBalance;
	
	private PetListModel model;
	private Map userMap;
	private Stage stage;
	private Thread t1;
	
	private final ObservableList<Pet> obList = FXCollections.observableArrayList();

	public PetListController() {
		t1 = new Thread(() -> {
			model = new PetListModel();
			System.out.print("petListController");
			this.imagePane.setVisible(true);
	        
			onGetList();
		});
		t1.start();
		
	}
	// get user info from LoginController and set data
	public void setUser(Map userMap) {
		this.userMap = userMap;
		System.out.println("setUser:" + userMap);
		String username = (String) userMap.get("username");
		Float balance = (Float) userMap.get("balance");
		this.txtUsername.setText(username);
		this.txtBalance.setText(String.valueOf(balance));
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	@FXML
    private void initialize(){
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        breedColumn.setCellValueFactory(cellData -> cellData.getValue().breed);
        priceColumn.setCellValueFactory(new PropertyValueFactory<Pet, Float>("price"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<Pet, Integer>("age"));
        operateColumn.setCellFactory(createButtonCellFactory());
        isSaledColumn.setCellValueFactory(new PropertyValueFactory<Pet, Boolean>("isSaled"));
        idColumn.setCellValueFactory(new PropertyValueFactory<Pet, Integer>("id"));
        
		System.out.println("setItems");
        //绑定数据到TableView
        petTable.setItems(obList);
    }

	// TableColoumn add Button
	private Callback<TableColumn<Pet, Boolean>, TableCell<Pet, Boolean>> createButtonCellFactory() {
        return new Callback<TableColumn<Pet, Boolean>, TableCell<Pet, Boolean>>() {
            @Override
            public TableCell<Pet, Boolean> call(final TableColumn<Pet, Boolean> param) {
                return new TableCell<Pet, Boolean>() {
                    private final Button btn = new Button("Buy it");

                    {
                        // 设置按钮点击事件
                        btn.setOnAction(event -> {
                            // 获取当前行的数据对象
                            Pet pet = getTableView().getItems().get(getIndex());
                            System.out.println("Button clicked for: " + pet.getName() + " " + pet.getIsSaled());
                            onPurchase(pet);
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
	
	@SuppressWarnings("unchecked")
	public void onPurchase(Pet pet) {
		t1.interrupt();
		Float balance = (Float) userMap.get("balance");
		Integer userId = (Integer) userMap.get("id");
		String username = (String) userMap.get("username");
		Integer petId = pet.getId();
		String name = pet.getName();
		Integer age = pet.getAge();
		Float price = pet.getPrice();
		String breed = pet.getBreed();
		Map petInfo = new HashMap();
		petInfo.put("petId", petId);
		System.out.println("petListController userId: " + userId);
		petInfo.put("userId", userId);
		petInfo.put("price", price);
		petInfo.put("balance", balance);
		petInfo.put("name", name);
		petInfo.put("breed", breed);
		petInfo.put("age", age);
		petInfo.put("buyer", username);
		model.queryCreateOrder(petInfo);
		if (balance < price) {
			System.out.println("balance < price");
			return;
		}
		
		
//		onGoOrder();
	}
	
	public void onGoOrder() {
		try {
			AnchorPane root;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/OrderView.fxml"));
			root = (AnchorPane) loader.load();
			Stage orderStage = new Stage();
//			Main.stage.setTitle("Order List View");
//			stage.setTitle("Pet Order View");
			OrderController orderController = loader.getController();
			orderController.setStage(stage);
			orderController.setOrderStage(orderStage);
			orderController.setUser(userMap);
			Scene scene = new Scene(root);
			orderStage.setScene(scene);
//			stage.setScene(scene);
			orderStage.show();
			stage.close();
//			Main.stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
	}
	
	public void alertCreate(Boolean isValid, String username) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Buy Tip");
		alert.setHeaderText(isValid ? "Buy Successfully" : "Buy Faild");
		alert.setContentText(isValid ? "Welcome, " + username + "!" : "The username or password is incorrect!");
		alert.showAndWait();
	}
	
	public void onGoBack() throws IOException {
		t1.interrupt();
		AnchorPane root;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
		root = (AnchorPane) loader.load();
		LoginController loginController = loader.getController();
		loginController.setStage(stage);
		Scene scene = new Scene(root);
		stage.setTitle("Customer Login View");
		stage.setScene(scene);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onGetList() {
		this.imagePane.setVisible(true);
		Vector<Map> data = model.queryPetList();
		System.out.println("getList: " + data);
		this.imagePane.setVisible(false);
		obList.remove(0, obList.size());
		for (int i = 0; i < data.size(); i++) {
			Integer id = (Integer) data.get(i).get("id");
			String name = (String) data.get(i).get("name");
			String breed = (String) data.get(i).get("breed");
			Float price = (Float) data.get(i).get("price");
			Integer age = (Integer) data.get(i).get("age");
			Boolean isSaled = (Boolean) data.get(i).get("isSaled");
			
			
			obList.add(new Pet(id, name, breed, price, age, isSaled));
			System.out.println("name: " + data.get(i).get(5) + ", isSaled: " + isSaled);
		}
		System.out.print("obList" + obList);
	}
	
	
	public static class Pet {
		private final SimpleIntegerProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty breed;
        private final SimpleFloatProperty price;
        private final SimpleIntegerProperty age;
        private final SimpleBooleanProperty isSaled;
//        private final SimpleDateProperty birthday;
 
        private Pet(Integer id, String name, String breed, Float price, Integer age, Boolean isSaled) {
        	System.out.println("Pet()");
//        	this.setName(name);
//        	this.setAge(age);
        	this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.breed = new SimpleStringProperty(breed);
            this.price = new SimpleFloatProperty(price);
            this.age = new SimpleIntegerProperty(age);
            this.isSaled = new SimpleBooleanProperty(isSaled);
//            this.birthday = new SimpleObjectProperty<LocalDate>(birthday);
        }
        
        public Integer getId() {
        	return id.get();
        }
        public void setId(Integer pId) {
        	age.set(pId);
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


