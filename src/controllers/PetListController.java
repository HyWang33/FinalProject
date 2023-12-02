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
import javafx.scene.control.TableRow;
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
	@FXML
	private Button createButton;
	@FXML
	private Label txtRole;
	
	private PetListModel model;
	private Map userMap;
	private Stage stage;
	private Thread t1;
	private Integer role = 1;
	
	private final ObservableList<Pet> obList = FXCollections.observableArrayList();

	public PetListController() {
		t1 = new Thread(() -> {
			model = new PetListModel();
			this.imagePane.setVisible(true);
	        
			onGetList();
		});
		t1.start();
		
	}
	// get user info from LoginController and set data
	public void setUser(Map userMap) {
		this.userMap = userMap;
		String username = (String) userMap.get("username");
		Float balance = (Float) userMap.get("balance");
		this.txtUsername.setText(username);
		this.txtBalance.setText(String.valueOf(balance) + "$");
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setRole(Integer role) {
		this.role = role;
		if (role == 2) {
			this.createButton.setVisible(true);
			this.txtRole.setText("Admin");
		}
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
        
        petTable.setItems(obList);
    }

	// TableColoumn add Button
	private Callback<TableColumn<Pet, Boolean>, TableCell<Pet, Boolean>> createButtonCellFactory() {
        return new Callback<TableColumn<Pet, Boolean>, TableCell<Pet, Boolean>>() {
            @Override
            public TableCell<Pet, Boolean> call(final TableColumn<Pet, Boolean> param) {
                return new TableCell<Pet, Boolean>() {
                    private final Button btn = new Button(role == 1 ? "Buy it" : "Update");
                    {
                        btn.setOnAction(event -> {
                            Pet pet = getTableView().getItems().get(getIndex());
                            if (role == 1) {
                            	onPurchase(pet);
                            } else {
                            	try {
									onGoUpdate(pet);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                            }
                        });
           
                    }

                    @Override
                    public void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                        	Pet currentPet = getTableView().getItems().get(getIndex());
//                        	TableRow<Pet> currentRow = getTableRow();
//                        	if (currentRow == null) return;
//                        	Pet currentPet = currentRow.getItem();
                        	System.out.println("index isSaled: " + getIndex()  + ", " + currentPet.getIsSaled());
                        	if (currentPet.getIsSaled()) {
                        		btn.setDisable(true);
                        	}
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onGetList() {
		this.imagePane.setVisible(true);
		Vector<Map> data = model.queryPetList();
		this.imagePane.setVisible(false);
		obList.remove(0, obList.size());
		obList.clear();
		petTable.getItems().clear();
		for (int i = 0; i < data.size(); i++) {
			Integer id = (Integer) data.get(i).get("id");
			String name = (String) data.get(i).get("name");
			String breed = (String) data.get(i).get("breed");
			Float price = (Float) data.get(i).get("price");
			Integer age = (Integer) data.get(i).get("age");
			Boolean isSaled = (Boolean) data.get(i).get("isSaled");
			
			
			obList.add(new Pet(id, name, breed, price, age, isSaled));
		}
		petTable.setItems(obList);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		
		if (balance < price) {
			System.out.println("balance < price");
			alertBalance();
			return;
		}
		
		Map petInfo = new HashMap();
		petInfo.put("petId", petId);
		petInfo.put("userId", userId);
		petInfo.put("price", price);
		petInfo.put("balance", balance);
		petInfo.put("name", name);
		petInfo.put("breed", breed);
		petInfo.put("age", age);
		petInfo.put("buyer", username);
		Boolean valid = model.queryCreateOrder(petInfo);
		BigDecimal decimalBalance = BigDecimal.valueOf(balance).subtract(BigDecimal.valueOf(price));
		decimalBalance = decimalBalance.setScale(2, RoundingMode.HALF_UP);
		float newBalance = decimalBalance.floatValue();
		userMap.put("balance", newBalance);
		this.txtBalance.setText(String.valueOf(newBalance) + "$");
		alertCreate(valid, name);
		onRefresh();
		
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
			orderStage.setTitle("Pet Order View");
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
		alert.setTitle("Purchase Tip");
		alert.setHeaderText(isValid ? "Purchase Successfully" : "Purchase Faild");
		alert.setContentText(isValid ? "Purchase " + username + " successfully!" : "Purchase faild! \n(Or check your network)");
		alert.showAndWait();
	}
	
	public void alertBalance() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Purchase Tip");
		alert.setHeaderText("Purchase Faild");
		alert.setContentText("Insufficient balance in your account.");
		alert.showAndWait();
	}
	
	public void onGoBack() throws IOException {
		t1.interrupt();
		AnchorPane root;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
		root = (AnchorPane) loader.load();
		LoginController loginController = loader.getController();
		loginController.setStage(stage);
		loginController.setRole(role);
		Scene scene = new Scene(root);
		stage.setTitle("Customer Login View");
		stage.setScene(scene);
	}
	
	public void onGoCreate() throws IOException {
		t1.interrupt();
		AnchorPane root;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PetCreateView.fxml"));
		root = (AnchorPane) loader.load();
		PetCreateController petCreateController = loader.getController();
		petCreateController.setStage(stage);
		petCreateController.setUser(userMap);
		petCreateController.setRole(role);
		Scene scene = new Scene(root);
		stage.setTitle("Pet Create View");
		stage.setScene(scene);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onGoUpdate(Pet pet) throws IOException {
		t1.interrupt();
		Map petMap = new HashMap();
		petMap.put("id", pet.getId());
		petMap.put("name", pet.getName());
		petMap.put("age", String.valueOf(pet.getAge()));
		petMap.put("breed", pet.getBreed());
		petMap.put("price", String.valueOf(pet.getPrice()));
		AnchorPane root;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PetCreateView.fxml"));
		root = (AnchorPane) loader.load();
		PetCreateController petCreateController = loader.getController();
		petCreateController.setStage(stage);
		petCreateController.setPet(petMap);
		petCreateController.setUser(userMap);
		petCreateController.setRole(role);
		Scene scene = new Scene(root);
		stage.setTitle("Pet Update View");
		stage.setScene(scene);
	}
	
	public void onUserUpdate() {
		t1.interrupt();
		try {
			AnchorPane root;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignUpView.fxml"));
			root = (AnchorPane) loader.load();
			stage.setTitle(role == 1 ? "Customer Sign Up View" : "Admin Sign Up View");
			SignUpController signUpController = loader.getController();
			signUpController.setStage(stage);
			signUpController.setUser(userMap);
			if (role == 2) {
				signUpController.setRole(role);
			}
			Scene scene = new Scene(root);
			stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
	}
	
	public void onRefresh() {
		try {
			AnchorPane root;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PetListView.fxml"));
			root = (AnchorPane) loader.load();
			stage.setTitle("Pet List View");
			PetListController petListController = loader.getController();
			petListController.setUser(userMap);
			petListController.setStage(stage);
			petListController.setRole(role);
			Scene scene = new Scene(root);
			stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
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
        	id.set(pId);
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


