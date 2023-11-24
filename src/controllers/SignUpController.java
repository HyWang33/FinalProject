package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
	
	public SignUpController() {
		model = new SignUpModel();
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
		if (email == null || email.trim().equals("")) {
			lblError.setText("Email Cannot be empty or spaces");
			return;
		}
		if (!emailValidator(email)) {
			lblError.setText("Email is not valid");
			return;
		}
		
		Map userMap = new HashMap();
		userMap.put("username", username);
		userMap.put("password", password);
		userMap.put("gender", gender);
		userMap.put("email", email);
		userMap.put("birthday", java.sql.Date.valueOf(birthday));
		model.createUser(userMap);
	}
	
	public boolean emailValidator(String email) {
		String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
