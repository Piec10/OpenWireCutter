package com.openwirecutter;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

import static com.openwirecutter.PropertiesXMLWriter.storePropertiesToXML;


public class MainWindowController {

    private Model model;
    private Stage stage;

    @FXML
    private TextField feederAxisNameTF;

    @FXML
    private TextField feederAxisFeedrateTF;

    @FXML
    private TextArea cutGCodeTA;

    @FXML
    private TextArea startingGCodeTA;

    @FXML
    private TextArea endingGCodeTA;

    @FXML
    private TextField lengthTF;

    @FXML
    private TextField quantityTF;

    @FXML
    private TextArea gCodeTA;

    @FXML
    private Button addOrChangeButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label infoLabel;

    @FXML
    private TableView<CutOrder> cutListTableView;

    @FXML
    private TableColumn<CutOrder, String> lengthColumn;

    @FXML
    private TableColumn<CutOrder, String> quantityColumn;

    @FXML
    private void initialize() {

        lengthColumn.setCellValueFactory(param -> new SimpleStringProperty(String.format(Locale.ROOT,"%.3f",param.getValue().getLength())));
        quantityColumn.setCellValueFactory(param -> new SimpleStringProperty(String.format(Locale.ROOT,"%d",param.getValue().getQuantity())));

        cutListTableView.setRowFactory(tableView -> {
            final TableRow<CutOrder> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                final int index = row.getIndex();
                if (index >= 0 && index < tableView.getItems().size() && tableView.getSelectionModel().isSelected(index)) {
                    tableView.getSelectionModel().clearSelection(index);
                    event.consume();
                }
            });
            return row;
        });

        cutListTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldOrder, newOrder) -> {
            if(newOrder == null){
                deleteButton.setDisable(true);
                lengthTF.setText("");
                quantityTF.setText("");
                addOrChangeButton.setText("Add");
            }
            else {
                deleteButton.setDisable(false);
                lengthTF.setText(Double.toString(newOrder.getLength()));
                quantityTF.setText(Integer.toString(newOrder.getQuantity()));
                addOrChangeButton.setText("Change");
            }
        });

        infoLabel.setText("Wire Cutter by @mateusz_piecka");
        cutListTableView.setPlaceholder(new Label(""));
    }

    public void init(Model model, Stage stage) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;

        this.stage = stage;

        cutListTableView.setItems(model.getCutList());

        model.gCodeProperty().addListener((obs, oldValue, newValue) -> {
            gCodeTA.clear();
            gCodeTA.setText(newValue);
        });

        feederAxisNameTF.setText(model.getFeederAxisName());
        feederAxisFeedrateTF.setText(model.getFeederAxisFeedrate());
        cutGCodeTA.setText(model.getCutGCode());
        startingGCodeTA.setText(model.getStartingGCode());
        endingGCodeTA.setText(model.getEndingGCode());

        lengthTF.requestFocus();
    }

    @FXML
    private void handleClose(){
        Platform.exit();
    }

    @FXML
    private void handleAddOrChange(){
        MultiTextFieldInputManager manager = new MultiTextFieldInputManager();

        manager.check("Length", lengthTF.getText(), InputType.POSITIVE_DOUBLE);
        manager.check("Quantity", quantityTF.getText(), InputType.POSITIVE_INTEGER);

        if(manager.isInputIncorrect()){
            manager.getAlert().showAndWait();
        }
        else{
            if(cutListTableView.getSelectionModel().selectedIndexProperty().get() == -1){
                model.addOrder(manager.getParsedValue(),manager.getParsedValue());

                lengthTF.clear();
                quantityTF.clear();
                lengthTF.requestFocus();
            }
            else{
                model.modifyOrder(cutListTableView.getSelectionModel().selectedIndexProperty().get(),manager.getParsedValue(),manager.getParsedValue());
                lengthTF.requestFocus();
            }
        }
    }

    @FXML
    public void handleDelete(){
        model.deleteOrder(cutListTableView.getSelectionModel().selectedIndexProperty().get());
        cutListTableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSaveSettings(){
        MultiTextFieldInputManager manager = new MultiTextFieldInputManager();

        manager.check("Feedrate", feederAxisFeedrateTF.getText(), InputType.POSITIVE_DOUBLE);

        if(manager.isInputIncorrect()){
            manager.getAlert().showAndWait();
        }
        else{
            model.setFeederAxisName(feederAxisNameTF.getText());
            model.setFeederAxisFeedrate(feederAxisFeedrateTF.getText());
            model.setCutGCode(cutGCodeTA.getText());
            model.setStartingGCode(startingGCodeTA.getText());
            model.setEndingGCode(endingGCodeTA.getText());

            Properties programSettings = model.getProgramSettings();
            storePropertiesToXML(programSettings,"settings.xml");
        }
    }

    @FXML
    private void handleSaveGCode(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("GCODE", "*.gcode"));
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));
        String initialFileName = ".gcode";
        fileChooser.setInitialFileName(initialFileName);

        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            TextFileWriter.saveTextFile(selectedFile, gCodeTA.getText());
        }
    }
}
