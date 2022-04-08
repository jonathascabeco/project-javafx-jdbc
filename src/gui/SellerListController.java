package gui;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;

	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<Seller, String> tableColumnName;

	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	// coluna de edi��o, ser� inserido um bot�o na mesma;

	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;

	@FXML
	private Button btNew;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		// precisa-se instanciar o objeto vazio para depois receber os dados do
		// formul�rio;
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	private ObservableList<Seller> obsList;

	// injetando dependencia, invers�o de controle;
	public void setSellerService(SellerService service) {
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
		// pegando referencia da janela, window � uma superclasse do stage, por isso o
		// downCasting;

		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
		// tableview aconpanhar a altura da janela;
	}

	public void updateTableView() {
		// prote��o caso n�o injete a depend�ncia;
		if (service == null) {
			throw new IllegalStateException("Service was null!");
		}

		List<Seller> list = service.findAll();
		// dados da lista mock do Seller;
		obsList = FXCollections.observableArrayList(list);
		// instanciando a list na obsList;
		tableViewSeller.setItems(obsList);
		// instanciando a obsList no view;
		initEditButtons();
		// instanciar botao edit em cada campo de itens department;
		initRemoveButtons();
		// instanciando bot�o delete em cada entidade do department;
	}

		
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
//		// informando o stage que criou a janela de dialogo;
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//
//			SellerFormController controller = loader.getController();
//			controller.setSeller(obj);
//			controller.setSellerService(new SellerService());// injetando a dependencia do servi�o;
//			controller.subscribeDataChangeListener(this);// ouvindo o evento - quando acionado ele executar� o
//															// onDataChanged();
//			controller.updateFormData();
//
//			// quando a janela � modal, precisa instanciar um novo stage; Um palco na frente
//			// do outro:
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Enter Seller data");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false);
//			dialogStage.initOwner(parentStage);
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.showAndWait();
//
//		} catch (IOException e) {
//			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	// bot�o em cada campo de itens do department com possibilidade de edi��o;
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				// department da linha do bot�o clicado;
				// pegando obj preenchido criando a tela de cadastro com o mesmo preenchido;

				if (obj == null) {
					setGraphic(null);
					return;
				}
				// configura��o do evento do bot�o;
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
				// quando clicar vai abrir esse formul�rio descrito;
			}
		});
	}

	// parecido com o m�todo acima, mas com fun��o de dele��o;
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
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

	// Opera��o para remover uma entidade de department;
	private void removeEntity(Seller obj) {
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
