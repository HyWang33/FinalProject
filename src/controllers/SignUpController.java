package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.SignUpModel;

public class SignUpController {
	@FXML
	private Label lblError;
	@FXML
	private TextField txtUsername;
	@FXML
	private PasswordField txtPassword;
	@FXML
	private ChoiceBox genderChoiceBox;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker datePicker;
	
	@FXML
    private void initialize(){
		genderChoiceBox.getItems().addAll("male", "female");
		genderChoiceBox.setValue("male");
	}
	
	private SignUpModel model;
	private Stage stage;
	private Map userMap = new HashMap();
	private Thread t1;
	private Integer role = 1;
	
	public SignUpController() {
		t1 = new Thread(()-> {
			model = new SignUpModel();
		});
		t1.start();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setUser(Map userMap) {
		this.userMap = userMap;
		if (userMap.containsKey("id")) {
			Date birthday = (Date) userMap.get("birthday");
			this.txtUsername.setText((String) userMap.get("username"));;
			this.txtPassword.setText((String) userMap.get("password"));
			this.genderChoiceBox.setValue((String) userMap.get("gender"));
			this.txtEmail.setText((String) userMap.get("email"));
			this.datePicker.setValue(birthday.toLocalDate());
			System.out.println("signup get userMap:" + userMap);
		}
	}
	
	public void setRole(Integer role) {
		this.role = role;
	}
	
	public void onClear() {
		this.txtUsername.setText("");;
		this.txtPassword.setText("");
		this.genderChoiceBox.setValue("male");
		this.txtEmail.setText("");
		this.datePicker.setValue(null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onSubmit() throws NoSuchAlgorithmException {
		lblError.setText("");
		String username = this.txtUsername.getText();
		String password = this.txtPassword.getText();
		String gender = (String) this.genderChoiceBox.getValue();
		String email = this.txtEmail.getText();
		LocalDate birthday = this.datePicker.getValue();
		
		if ((username == null || username.trim().equals("")) && (password == null || password.trim().equals(""))) {
			lblError.setText("Username / Password Cannot be empty or spaces");
			return;
		}
		if (username == null || username.trim().equals("")) {
			lblError.setText("Username Cannot be empty or spaces");
			return;
		}
		if (password == null || password.trim().equals("")) {
			lblError.setText("Password Cannot be empty or spaces");
			return;
		}
//		if (email == null || email.trim().equals("")) {
//			lblError.setText("Email Cannot be empty or spaces");
//			return;
//		}
		if (email != null && !email.trim().equals("") && !emailValidator(email)) {
			lblError.setText("Email is not valid");
			return;
		}
		
		Map userInfo = new HashMap();
		userInfo.put("username", username);
		userInfo.put("password", password);
		userInfo.put("gender", gender);
		userInfo.put("email", email);
		userInfo.put("birthday", java.sql.Date.valueOf(birthday));
		userInfo.put("role", role);
		if (userMap.containsKey("id")) {
			userInfo.put("id", userMap.get("id"));
			Boolean res = model.updateUser(userInfo);
			alertUpdate(res, username);
		} else {
			Boolean res = model.createUser(userInfo);
			alertCreate(res, username);
		}
	}
	
	public boolean emailValidator(String email) {
		String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	public void onGoBack() throws IOException {
		if (role == 3 && userMap.containsKey("id")) {
			onGoUserList();
			return;
		}
		if (userMap.containsKey("id")) {
			onGoPet();
			return;
		}
		t1.interrupt();
		AnchorPane root;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
		root = (AnchorPane) loader.load();
		LoginController loginController = loader.getController();
		loginController.setStage(stage);
		loginController.setRole(role);
		Scene scene = new Scene(root);
		if (role == 3) {
			stage.setTitle("Manager Login View");
		} else {
			stage.setTitle(role == 1 ? "Customer Login View" : "Admin Login View");
		}
		
		stage.setScene(scene);
	}
	
	public void onGoPet() throws IOException {
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
	}
	
	public void onGoUserList() {
		try {
			AnchorPane root;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserListView.fxml"));
			root = (AnchorPane) loader.load();
			stage.setTitle("User List View");
			UserListController userListController = loader.getController();
			userListController.setUser(userMap);
			userListController.setStage(stage);
			userListController.setRole(role);
			Scene scene = new Scene(root);
			stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
	}
	
	public void alertCreate(Boolean isValid, String username) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Sign Up Tip");
		alert.setHeaderText(isValid ? "Sign Up Successfully" : "Sign Up Faild");
		alert.setContentText(isValid ? "Congratulation, " + username + " is created!" : "Admin Sign Up Failed");
		alert.showAndWait();
	}
	public void alertUpdate(Boolean isValid, String username) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Update Tip");
		alert.setHeaderText(isValid ? "Update Successfully" : "Update Faild");
		alert.setContentText(isValid ? "Congratulation, " + username + " is updated!" : "Admin Update Failed");
		alert.showAndWait();
	}
}
