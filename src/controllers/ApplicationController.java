package controllers;

import java.io.IOException;

import application.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ApplicationController {
	private Stage stage;
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void onGoCustomer() throws IOException {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/LoginView.fxml"));
        AnchorPane root = (AnchorPane) loader.load();
		Scene scene = new Scene(root);
		LoginController loginController = loader.getController();
		loginController.setStage(stage);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		stage.setTitle("Customer Login View");
		stage.setScene(scene);
		stage.show();
	}
	
	public void onGoAdmin() throws IOException {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/LoginView.fxml"));
        AnchorPane root = (AnchorPane) loader.load();
		Scene scene = new Scene(root);
		LoginController loginController = loader.getController();
		loginController.setStage(stage);
		loginController.setRole(2);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		stage.setTitle("Admin Login View");
		stage.setScene(scene);
		stage.show();
	}
	
	public void onGoManager() throws IOException {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/LoginView.fxml"));
        AnchorPane root = (AnchorPane) loader.load();
		Scene scene = new Scene(root);
		LoginController loginController = loader.getController();
		loginController.setStage(stage);
		loginController.setRole(3);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		stage.setTitle("Manager Login View");
		stage.setScene(scene);
		stage.show();
	}
	
	public void onExit() {
		System.exit(0);
	}
}
