package controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.PetCreateModel;

public class PetCreateController {
	@FXML
	private Label lblError;
	@FXML
	private TextField txtName;
	@FXML
	private ChoiceBox breedChoiceBox;
	@FXML
	private TextField txtPrice;
	@FXML
	private TextField txtAge;
	@FXML
	private Button deleteButton;
	
	@FXML
    private void initialize(){
		breedChoiceBox.getItems().addAll("cat", "dog");
		breedChoiceBox.setValue("cat");
	}
	
	private PetCreateModel model;
	private Stage stage;
	private Map userMap;
	private Thread t1;
	private Integer role = 2;
	private Map petMap = new HashMap();
	
	public PetCreateController() {
		t1 = new Thread(()-> {
			model = new PetCreateModel();
		});
		t1.start();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setUser(Map userMap) {
		this.userMap = userMap;
	}
	
	public void setRole(Integer role) {
		this.role = role;
	}
	
	public void setPet(Map petMap) {
		this.petMap = petMap;
		String name = (String) petMap.get("name");
		String age = (String) petMap.get("age");
		String price = (String) petMap.get("price");
		String breed = (String) petMap.get("breed");
		this.txtName.setText(name);;
		this.txtPrice.setText(price);
		this.breedChoiceBox.setValue(breed);
		this.txtAge.setText(age);
		if (petMap.containsKey("id")) {
			if (role == 2) {
				deleteButton.setVisible(true);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onClear() {
		this.txtName.setText("");;
		this.txtPrice.setText("");
		this.breedChoiceBox.setValue("cat");
		this.txtAge.setText("");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onSubmit() throws NoSuchAlgorithmException {
		lblError.setText("");
		String petName = this.txtName.getText();
		String breed = (String) this.breedChoiceBox.getValue();
		String price = this.txtPrice.getText();
		String age = this.txtAge.getText();
		
		if (petName == null || petName.trim().equals("")) {
			lblError.setText("Pet name Cannot be empty or spaces");
			return;
		}
		if (age == null || age.equals("")) {
			lblError.setText("Pet age Cannot be empty or spaces");
			return;
		}
		if (price == null || price.trim().equals("")) {
			lblError.setText("Pet price Cannot be empty or spaces");
			return;
		}
		if (!ageValidator(age)) {
			lblError.setText("Pet age is not valid");
			return;
		}
		if (!priceValidator(price) || Float.parseFloat(price) <= 0) {
			lblError.setText("Pet price is not valid");
			return;
		}
		
		Map petInfo = new HashMap();
		petInfo.put("name", petName);
		petInfo.put("price", Float.parseFloat(price));
		petInfo.put("breed", breed);
		petInfo.put("age", Integer.parseInt(age));
		Boolean res = false;
		if (petMap.containsKey("id")) {
			petInfo.put("id", petMap.get("id"));
			res = model.updatePet(petInfo);
		} else {
			res = model.createPet(petInfo);
		}
		alertCreate(res, petName);
	}
	
	public boolean priceValidator(String email) {
		String EMAIL_REGEX = "\\d+(\\.\\d{1,2})?";
		Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	public boolean ageValidator(String email) {
		String EMAIL_REGEX = "^[1-9](\\d)*$";
		Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	public void onGoBack() throws IOException {
		t1.interrupt();
		AnchorPane root;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PetListView.fxml"));
		root = (AnchorPane) loader.load();
		PetListController petListController = loader.getController();
		petListController.setStage(stage);
		petListController.setRole(2);
		petListController.setUser(userMap);
		Scene scene = new Scene(root);
		stage.setTitle("Pet List View");
		stage.setScene(scene);
	}
	
	public void onDelete() throws IOException {
		t1.interrupt();
		Integer petId = (Integer) petMap.get("id");
		String name = (String) petMap.get("name");
		Boolean res = model.deletePet(petId);
		alertDelete(res, name);
		onGoBack();
	}
	
	public void alertCreate(Boolean isValid, String username) {
		Alert alert = new Alert(AlertType.INFORMATION);
		String operateText = petMap.containsKey("id") ? "Update" : "Create";
		alert.setTitle(petMap.containsKey("id") ?"Update pet tip" : "Create pet tip");
		alert.setHeaderText(isValid ? operateText + " Successfully" : operateText + " Faild");
		alert.setContentText(isValid ? operateText + " " + username + " successfully!" : operateText + " Faild");
		alert.showAndWait();
	}
	
	public void alertDelete(Boolean isValid, String username) {
		Alert alert = new Alert(AlertType.INFORMATION);
		String operateText = petMap.containsKey("id") ? "Update" : "Create";
		alert.setTitle("Delete tip");
		alert.setHeaderText(isValid ?"Delete Successfully" : "Delete Faild");
		alert.setContentText("Delete " + username + (isValid ? " successfully!" :" Faild!"));
		alert.showAndWait();
	}
}
