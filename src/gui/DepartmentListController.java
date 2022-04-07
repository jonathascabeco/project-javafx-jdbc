package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {
	
	private DepartmentService service;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		createDialogForm("/gui/DepartmentForm.fxml", parentStage);
	}	
	
	private ObservableList<Department> obsList;
		
	//injetando dependencia, invers�o de controle;
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();	
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		// rotina padr�o javafx para iniciar o comportamento das colunas;
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		//pegando referencia da janela, window � uma superclasse do stage, por isso o downCasting;
		
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		//tableview aconpanhar a altura da janela;
	}
	
	public void updateTableView() {
		//prote��o caso n�o injete a depend�ncia;
		if(service == null) {
			throw new IllegalStateException("Service was null!");
		}
		
		List<Department> list = service.findAll();
		//dados da lista mock do Department;
		obsList = FXCollections.observableArrayList(list);
		//instanciando a list na obsList;
		tableViewDepartment.setItems(obsList);
		//instanciando a obsList no view;		
	}
	
	private void createDialogForm(String absoluteName, Stage parentStage) {
	//informando o stage que criou a janela de dialogo;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			//quando a janela � modal, precisa instanciar um novo stage; Um palco na frente do outro:
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			
		}catch(IOException e) {
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
