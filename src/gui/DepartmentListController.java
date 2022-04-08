package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;
	// coluna de edição, será inserido um botão na mesma;

	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;

	@FXML
	private Button btNew;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		// precisa-se instanciar o objeto vazio para depois receber os dados do
		// formulário;
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	private ObservableList<Department> obsList;

	// injetando dependencia, inversão de controle;
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
		// rotina padrão javafx para iniciar o comportamento das colunas;

		Stage stage = (Stage) Main.getMainScene().getWindow();
		// pegando referencia da janela, window é uma superclasse do stage, por isso o
		// downCasting;

		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		// tableview aconpanhar a altura da janela;
	}

	public void updateTableView() {
		// proteção caso não injete a dependência;
		if (service == null) {
			throw new IllegalStateException("Service was null!");
		}

		List<Department> list = service.findAll();
		// dados da lista mock do Department;
		obsList = FXCollections.observableArrayList(list);
		// instanciando a list na obsList;
		tableViewDepartment.setItems(obsList);
		// instanciando a obsList no view;
		initEditButtons();
		// instanciar botao edit em cada campo de itens department;
		initRemoveButtons();
		// instanciando botão delete em cada entidade do department;
	}

	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		// informando o stage que criou a janela de dialogo;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());// injetando a dependencia do serviço;
			controller.subscribeDataChangeListener(this);// ouvindo o evento - quando acionado ele executará o
															// onDataChanged();
			controller.updateFormData();

			// quando a janela é modal, precisa instanciar um novo stage; Um palco na frente
			// do outro:
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	// botão em cada campo de itens do department com possibilidade de edição;
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				// department da linha do botão clicado;
				// pegando obj preenchido criando a tela de cadastro com o mesmo preenchido;

				if (obj == null) {
					setGraphic(null);
					return;
				}
				// configuração do evento do botão;
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
				// quando clicar vai abrir esse formulário descrito;
			}
		});
	}

	// parecido com o método acima, mas com função de deleção;
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);

				if (obj == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));

			}
		});
	}

	// Operação para remover uma entidade de department;
	private void removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);

			}

		}
	}
}
