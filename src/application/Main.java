package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.LoginModel;
import models.OrderModel;
import models.PetListModel;

public class Main extends Application {
	
	public static Stage stage;
	
	@Override
	public void start(Stage primaryStage) {
		try {

			stage = primaryStage;
			//AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("/views/LoginView.fxml"));
		    FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/LoginView.fxml"));
	        AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setTitle("Login View");
			stage.setScene(scene);
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
//		createUserTab();
//		createPetTab();
//		createOrderTab();
	}
	
	public static void createUserTab() {
		LoginModel userModel = new LoginModel();
		userModel.createTable();
	}
	
	public static void createPetTab() {
		PetListModel petModel = new PetListModel();
		petModel.createTable();
	}
	
	public static void createOrderTab() {
		OrderModel orderModel = new OrderModel();
		orderModel.createTable();
	}
}
