package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;
	
	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListeners =new ArrayList<>(); 
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
		
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
		// objetos que implementam a interface do parametro, podem se inscrever para receber o evento da classe me quest�o;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@FXML
	private void onBtSaveAction(ActionEvent event) {
		//tratando exce��o, pois a inje��o de independencia � manual;
		//se houvesse um framework(conteiners) para tratar nao seria necess�rio;
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifayDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch(DbException e){
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifayDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
			//implementando a atualiza��o em cada item da lista;
		}
		
	}

	private Department getFormData() {
		Department obj = new Department();
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		//pegando o texto na caixa do form e utilizando a fun��o para transform�-lo em n�mero inteiro;
		
		obj.setName(txtName.getText());
		
		return obj;		
	}

	@FXML
	private void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();//
	}	
	
	@Override
	public void initialize(URL url, ResourceBundle rs) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));//string.value porque pega o inteiro e transforma pra int;
		txtName.setText(entity.getName());
	}
}
