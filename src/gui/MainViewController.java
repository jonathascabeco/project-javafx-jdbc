package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {
	
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemItemAbout;
	
	
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml");
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
	
	private synchronized void loadView(String absoluteName) {
		// sybchronized impede a interrupi��o do processamento;
		//absolute porque o nome que ser� passado vai ser o completo;
		try{
		FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
		VBox newVBox = loader.load();
		//carregando a view;
		
		Scene mainScene = Main.getMainScene();
		VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
		//pegando conte�do(MainView) dentro do ScrollPane e depois dentro do content, que no caso � o VBox, por isso o casting;
		
		// Limpando a mainview, e inseriendo novamente o menuBar da mainview juntamamente com a aboutView;
		Node mainMenu = mainVBox.getChildren().get(0);
		mainVBox.getChildren().clear();
		mainVBox.getChildren().add(mainMenu);
		mainVBox.getChildren().addAll(newVBox.getChildren());		
		}
		catch(IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR );
			//tratando exce��o;
		}
	}

}
