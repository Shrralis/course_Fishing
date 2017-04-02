package main_form;

import base.AlertsBuilder;
import base.DataFormComboBoxControllerAdditional;
import base.DataFormControllerInterface;
import base.OnMouseClickListener;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.*;
import server.DatabaseWorker;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class Controller {
    @FXML private TabPane tabs;
    @FXML private TableView<FishingCondition> tableFishingConditions;
    @FXML private TableView<Fish> tableFishs;

    @FXML
    private void onButtonAddClick() throws IOException {
        openDataForm(DataFormControllerInterface.Type.Add);
    }
    @FXML
    private void onButtonEditClick() throws IOException {
        openDataForm(DataFormControllerInterface.Type.Edit);
    }
    @FXML
    private void onButtonDeleteClick() {
        String sSelectedTab = tabs.getSelectionModel().getSelectedItem().getText();

        if (sSelectedTab.equalsIgnoreCase("умови лову")) {
            deleteRecord(tableFishingConditions);
        } else {
            deleteRecord(tableFishs);
        }
    }
    @FXML
    private void onButtonSearchClick() throws IOException {
        openDataForm(DataFormControllerInterface.Type.Search);
    }
    @FXML
    private void onButtonRefreshClick() {
        String sSelectedTab = tabs.getSelectionModel().getSelectedItem().getText();

        if (sSelectedTab.equalsIgnoreCase("умови лову")) {
            loadDataToTableFromDatabase(tableFishingConditions, null);
        } else {
            loadDataToTableFromDatabase(tableFishs, null);
        }
    }

    private void openDataForm(DataFormControllerInterface.Type type) throws IOException {
        String sSelectedTab = tabs.getSelectionModel().getSelectedItem().getText();
        TableView tableView;
        FXMLLoader loader;

        if (sSelectedTab.equalsIgnoreCase("умови лову")) {
            tableView = tableFishingConditions;
            loader = new FXMLLoader(getClass().getResource("/fishing_conditions_data_form/data.fxml"));
        } else {
            tableView = tableFishs;
            loader = new FXMLLoader(getClass().getResource("/fishs_data_form/data.fxml"));
        }
        loader.load();

        DataFormControllerInterface controller = loader.getController();
        Stage dataFormStage = new Stage();

        if (type == DataFormControllerInterface.Type.Edit) {
            if (tableView.getSelectionModel().getSelectedItem() == null) {
                AlertsBuilder.start()
                        .setAlertType(Alert.AlertType.WARNING)
                        .setTitle("Помилка")
                        .setHeaderText("Помилка вибору")
                        .setContentText("Для редагування не було вибрано жодного значення у таблиці зверху!")
                        .build()
                        .showAndWait();
                return;
            }
            controller.setObjectToProcess((Owner) tableView.getSelectionModel().getSelectedItem());
        }
        setDataFormClickListeners(controller, type);
        dataFormStage.setScene(new Scene(loader.getRoot()));
        controller.setType(type);
        controller.setPrimaryStage(dataFormStage);
        dataFormStage.show();
    }

    private void setDataFormClickListeners(DataFormControllerInterface controller, DataFormControllerInterface.Type type) {
        OnMouseClickListener okListener = null;
        String sSelectedTab = tabs.getSelectionModel().getSelectedItem().getText();
        TableView tableView;

        if (sSelectedTab.equalsIgnoreCase("умови лову")) {
            tableView = tableFishingConditions;
        } else {
            tableView = tableFishs;
        }

        switch (type) {
            case Add:
                okListener = () -> controller.addObjectToTableView(tableView);
                break;
            case Edit:
                okListener = () -> controller.editObjectInTableView(tableView);
                break;
            case Search:
                okListener = () -> controller.search(this, tableView);
                break;
        }
        controller.setOnMouseOkClickListener(okListener);
    }

    void setupAllTables() {
        loadDataToTableFromDatabase(tableFishingConditions, null);
        loadDataToTableFromDatabase(tableFishs, null);
    }
    @SuppressWarnings("unchecked")
    public void loadDataToTableFromDatabase(TableView tableView, HashMap<String, Object> params) {
        for (Field field : getClass().getDeclaredFields()) {
            try {
                if (field.getType() == TableView.class && field.get(this) == tableView) {
                    tableView.setItems(
                            FXCollections.observableArrayList(
                                    DatabaseWorker.processQuery(
                                            ServerQuery.create(
                                                    field.getName().substring("table".length()).toLowerCase(),
                                                    "get", null, params
                                            )
                                    ).getObjects()
                            )
                    );
                    break;
                }
            } catch (IllegalAccessException ignored) {}
        }
    }

    private void deleteRecord(TableView tableView) {
        String tableName = null;

        for (Field field : getClass().getDeclaredFields()) {
            try {
                if (field.getType() == TableView.class && field.get(this) == tableView) {
                    tableName = field.getName().substring("table".length()).toLowerCase();

                    break;
                }
            } catch (IllegalAccessException ignored) {}
        }

        ServerResult result = DatabaseWorker.processQuery(
                ServerQuery.create(
                        tableName,
                        "delete",
                        (Owner) tableView.getSelectionModel().getSelectedItem(),
                        null
                )
        );

        if (result != null && result.getResult() == 0) {
            tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
        } else {
            AlertsBuilder.start()
                    .setAlertType(Alert.AlertType.WARNING)
                    .setTitle("Видалення")
                    .setHeaderText("Помилка видалення")
                    .build()
                    .showAndWait();
        }
    }
}
