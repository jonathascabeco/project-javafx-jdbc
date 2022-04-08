package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;
	
	private SellerService service;
	
	private List<DataChangeListener> dataChangeListeners =new ArrayList<>(); 
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
		
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
		
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
		// objetos que implementam a interface do parametro, podem se inscrever para receber o evento da classe me quest�o;
	}
	
	public void setSellerService(SellerService service) {
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
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
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

	private Seller getFormData() {
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error!");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		//pegando o texto na caixa do form e utilizando a fun��o para transform�-lo em n�mero inteiro;
		
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty!");
		}
		
		obj.setName(txtName.getText());
		
		if(exception.getErrors().size()>0) {
			throw exception;
		}
		
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
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
	}

	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		// pegando os dados digitados na UI e enviando para o obj, e esta instanciando para o BD;
		txtId.setText(String.valueOf(entity.getId()));//string.value porque pega o inteiro e transforma pra int;
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if(entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
	}
}
	
	private void setErrorMessages(Map<String, String> errors) {
		//percorrer a cole��o setando os campos labls correspondentes das mesmas na UI;
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
			//pegando a mensagem de erro do campo name e setando na label correspondente na UI;
		}		
	}
}
