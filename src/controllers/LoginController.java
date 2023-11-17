package controllers;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import models.LoginModel;

public class LoginController {
	@FXML
	private TextField txtUsername;

	@FXML
	private TextField txtPassword;

	@FXML
	private Label lblError;
	
	private LoginModel model;

	public LoginController() {
		model = new LoginModel();
	}
	
	public void onSubmit() {
		lblError.setText("");
		String username = this.txtUsername.getText();
		String password = this.txtPassword.getText();
		
		if ((username == null || username.trim().equals("")) && (password == null || password.trim().equals(""))) {
			lblError.setText("Username / Password Cannot be empty or spaces");
			return;
		}
		if (username == null || username.trim().equals("")) {
			lblError.setText("Username Cannot be empty or spaces");
			return;
		}
		if (password == null || password.trim().equals("")) {
			lblError.setText("Password Cannot be empaty or spaces");
			return;
		}
		checkCredentials(username, password);
		
		System.out.println("click submit");
		
	}
	
	public void checkCredentials(String username, String password) {
//		Boolean isValid = model.queryUser(username, password);
//		alertCreate(isValid);
//		if (!isValid) return;
		try {
			AnchorPane root;
			root = (AnchorPane) FXMLLoader.load(getClass().getResource("/views/PetListView.fxml"));
			Main.stage.setTitle("Pet List View");
			Scene scene = new Scene(root);
			Main.stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
//		System.out.println(isValid ? "登录成功" : "登录失败");
	}
	
	public void alertCreate(Boolean isValid) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Login Tip");
		alert.setHeaderText(isValid ? "Login Successfully" : "Login Faild");
		alert.setContentText(isValid ? "Welcome !" : "The username or password is incorrect!");
		alert.showAndWait();
	}
	
}
