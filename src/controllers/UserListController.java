package controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.UserListModel;

public class UserListController {
	@FXML
	private TableView<User> petTable;
	@FXML
    private TableColumn<User, String> nameColumn;
	@FXML
    private TableColumn<User, String> genderColumn;
	@FXML
    private TableColumn<User, Float> balanceColumn;
	@FXML
    private TableColumn<User, String> emailColumn;
	@FXML
	private TableColumn<User, String> roleColumn;
	@FXML
	private TableColumn<User, Boolean> operateColumn;
	@FXML
	private TableColumn<User, Integer> idColumn;
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
	
	private UserListModel model;
	private Map userMap;
	private Stage stage;
	private Thread t1;
	private Integer role = 1;
	
	private final ObservableList<User> obList = FXCollections.observableArrayList();

	public UserListController() {
		t1 = new Thread(() -> {
			model = new UserListModel();
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
	
	public void setRole(Integer role) {
		this.role = role;
		if (role == 2) {
			this.createButton.setVisible(true);
			this.txtRole.setText("Admin");
		}
		if (role == 3) {
			this.txtRole.setText("Manager");
		}
	}
	
	@FXML
    private void initialize(){
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().username);
        genderColumn.setCellValueFactory(cellData -> cellData.getValue().gender);
        balanceColumn.setCellValueFactory(new PropertyValueFactory<User, Float>("balance"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("emial"));
        operateColumn.setCellFactory(createButtonCellFactory());
        roleColumn.setCellValueFactory(new PropertyValueFactory<User, String>("role"));
        idColumn.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
        
        petTable.setItems(obList);
    }

	// TableColoumn add Button
	private Callback<TableColumn<User, Boolean>, TableCell<User, Boolean>> createButtonCellFactory() {
        return new Callback<TableColumn<User, Boolean>, TableCell<User, Boolean>>() {
            @Override
            public TableCell<User, Boolean> call(final TableColumn<User, Boolean> param) {
                return new TableCell<User, Boolean>() {
                    private final Button btn = new Button("Add Balance");
                    {
                        btn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
//                            System.out.println("Button clicked for: " + pet.getName() + " " + pet.getIsSaled());
                  
                            onOpenDialog(user);
                        });
           
                    }

                    @Override
                    public void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                        	User currentPet = getTableView().getItems().get(getIndex());
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
		Vector<Map> data = model.queryUserList();
		this.imagePane.setVisible(false);
		obList.remove(0, obList.size());
		obList.clear();
		for (int i = 0; i < data.size(); i++) {
			Integer id = (Integer) data.get(i).get("id");
			String username = (String) data.get(i).get("username");
			String gender = (String) data.get(i).get("gender");
			Float balance = (Float) data.get(i).get("balance");
			String email = (String) data.get(i).get("email");
			Integer role = (Integer) data.get(i).get("role");
			String roleText = role == 1 ? "Customer" : "Admin";
			
			obList.add(new User(id, username, gender, email, roleText, balance));
		}
	}
	
	public void onOpenDialog(User user) {
		String username = (String) user.getName();
		Integer id = (Integer) user.getId();
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Add balance");
		dialog.setHeaderText("Enter balance");
		dialog.setContentText("Please enter balance for: " + username);
		
        TextField textField = dialog.getEditor();
        Pattern pattern = Pattern.compile("\\d+\\.{0,1}(\\d{1,2})?");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (pattern.matcher(change.getControlNewText()).matches() || change.getControlNewText().equals("") ) {
                return change;
            } else {
                return null;
            }
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
		
        dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (textField.getText().isEmpty()) {
                event.consume();
            }
        });
        
		Optional<String> result = dialog.showAndWait();
		
		
		result.ifPresent(value -> {
			Float balance = Float.parseFloat(value);
			Boolean res = model.updateBalance(id, balance);
			alertCreate(res, username, balance);
			onGetList();
		});
		if (result.isPresent()) {
			System.out.println("Balance entry: " + result.get());
		}
	}
	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public void onPurchase(User pet) {
//		t1.interrupt();
//		Float balance = (Float) userMap.get("balance");
//		Integer userId = (Integer) userMap.get("id");
//		String username = (String) userMap.get("username");
//		Integer petId = pet.getId();
//		String name = pet.getName();
//		Integer age = pet.getAge();
//		Float price = pet.getPrice();
//		String breed = pet.getBreed();
//		
//		if (balance < price) {
//			System.out.println("balance < price");
//			return;
//		}
//		
//		Map petInfo = new HashMap();
//		petInfo.put("petId", petId);
//		petInfo.put("userId", userId);
//		petInfo.put("price", price);
//		petInfo.put("balance", balance);
//		petInfo.put("name", name);
//		petInfo.put("breed", breed);
//		petInfo.put("age", age);
//		petInfo.put("buyer", username);
//		model.queryCreateOrder(petInfo);
//		userMap.put("balance", balance - price);
//		this.txtBalance.setText(String.valueOf(balance - price));
//		
////		onGoOrder();
//	}
	
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
	
	public void alertCreate(Boolean isValid, String username, Float balance) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Update Balance Tip");
		alert.setHeaderText(isValid ? "Update Successfully" : "Update Faild");
		alert.setContentText(isValid ? "Update " + username + "'s balance is " + balance + "$" : "Update balance faild");
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
	
	public void onGoCreate() throws IOException {
		t1.interrupt();
		AnchorPane root;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PetCreateView.fxml"));
		root = (AnchorPane) loader.load();
		PetCreateController petCreateController = loader.getController();
		petCreateController.setStage(stage);
		petCreateController.setUser(userMap);
		Scene scene = new Scene(root);
		stage.setTitle("Pet Create View");
		stage.setScene(scene);
	}
	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public void onGoUpdate(User pet) throws IOException {
//		t1.interrupt();
//		Map petMap = new HashMap();
//		petMap.put("id", pet.getId());
//		petMap.put("user", pet.getName());
//		petMap.put("age", String.valueOf(pet.getAge()));
//		petMap.put("breed", pet.getBreed());
//		petMap.put("price", String.valueOf(pet.getPrice()));
//		AnchorPane root;
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PetCreateView.fxml"));
//		root = (AnchorPane) loader.load();
//		PetCreateController petCreateController = loader.getController();
//		petCreateController.setStage(stage);
//		petCreateController.setPet(petMap);
//		petCreateController.setUser(userMap);
//		Scene scene = new Scene(root);
//		stage.setTitle("Pet Update View");
//		stage.setScene(scene);
//	}
	
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
			signUpController.setRole(role);
			Scene scene = new Scene(root);
			stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
	}
	
	public static class User {
		private final SimpleIntegerProperty id;
        private final SimpleStringProperty username;
        private final SimpleStringProperty gender;
        private final SimpleFloatProperty balance;
        private final SimpleStringProperty role;
        private final SimpleStringProperty email;
//        private final SimpleDateProperty birthday;
 
        private User(Integer id, String username, String gender, String email, String role, Float balance) {
//        	this.setName(name);
//        	this.setAge(age);
        	this.id = new SimpleIntegerProperty(id);
            this.username = new SimpleStringProperty(username);
            this.gender = new SimpleStringProperty(gender);
            this.email = new SimpleStringProperty(email);
            this.balance = new SimpleFloatProperty(balance);
            this.role = new SimpleStringProperty(role);
//            this.isSaled = new SimpleBooleanProperty(isSaled)
//            this.birthday = new SimpleObjectProperty<LocalDate>(birthday);
        }
        
        public Integer getId() {
        	return id.get();
        }
        public void setId(Integer pId) {
        	id.set(pId);
        }
        
        public String getName() {
            return username.get();
        }
 
        public void setName(String pName) {
            username.set(pName);
        }
 
        public String getGender() {
            return gender.get();
        }
 
        public void setGender(String pGender) {
            gender.set(pGender);
        }
 
        public Float getBalance() {
            return balance.get();
        }
 
        public void setBalance(Float pBalance) {
            balance.set(pBalance);
        }
        
        public String getEmail() {
        	return email.get();
        }
        public void setAge(String pEmail) {
        	email.set(pEmail);
        }
        
        public String getRole() {
        	return role.get();
        }
        public void setRole(String pRole) {
        	role.set(pRole);
        }
    }
	
	
}


