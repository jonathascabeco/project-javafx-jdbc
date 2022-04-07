package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			//instanciando o carregamento da MainView;
			//Parent tracada por ScrollPane para o mesmo ja ser instanciado a principio;
			//Objetivando a configuração mais facilidata do layout principal;
			
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			//ajustando o vBox para preencher toda a tela do principal do View;
			
			Scene mainScene = new Scene(scrollPane);//obj Scene, cena principal;
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Sample JavaFX application");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
