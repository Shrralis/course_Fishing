package fishs_data_form;

import base.AlertsBuilder;
import base.DataFormController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import models.Fish;

/**
 * Created by shrralis on 3/13/17.
 */
@SuppressWarnings("unused")
public class Controller extends DataFormController {
    {
        objectToProcess = new Fish();
    }

    @FXML public TextField type;
    @FXML public TextField row;
    @FXML public TextField name;
    @FXML public TextField area;
    @FXML public TextField description;
    @FXML public TextField max_weight;
    @FXML public TextField other;
    @FXML public CheckBox bType;
    @FXML public CheckBox bRow;
    @FXML public CheckBox bName;
    @FXML public CheckBox bArsenal;
    @FXML public CheckBox bDescription;
    @FXML public CheckBox bMax_weight;
    @FXML public CheckBox bOther;

    @Override
    protected boolean isAnyTextFieldEmpty() {
        boolean empty = false;
        String fieldDaoName = "";

        if (type.getText().equalsIgnoreCase("")) {
            empty = true;
            fieldDaoName = type.getPromptText();
        } else if (row.getText().equalsIgnoreCase("")) {
            empty = true;
            fieldDaoName = row.getPromptText();
        } else if (name.getText().equalsIgnoreCase("")) {
            empty = true;
            fieldDaoName = name.getPromptText();
        } else if (area.getText().equalsIgnoreCase("")) {
            empty = true;
            fieldDaoName = area.getPromptText();
        } else if (description.getText().equalsIgnoreCase("")) {
            empty = true;
            fieldDaoName = description.getPromptText();
        } else if (max_weight.getText().equalsIgnoreCase("")) {
            empty = true;
            fieldDaoName = max_weight.getPromptText();
        }

        if (empty) {
            AlertsBuilder.start()
                    .setAlertType(Alert.AlertType.WARNING)
                    .setTitle("Помилка")
                    .setHeaderText("Помилка вводу")
                    .setContentText("Ви залишили поле " + fieldDaoName + " порожнім!")
                    .buildOnFront(primaryStage)
                    .showAndWait();
        }
        return empty;
    }
    @Override
    protected boolean areTheFieldsValidForAdding() {
        if (isAnyTextFieldEmpty()) {
            return false;
        }

        if (!max_weight.getText().trim().matches("^\\d+(\\.\\d+)?$")) {
            AlertsBuilder.start()
                    .setAlertType(Alert.AlertType.WARNING)
                    .setTitle("Помилка")
                    .setHeaderText("Помилка вводу")
                    .setContentText("Поле " + max_weight.getPromptText() + " повинно містити ціле або дробове число!\n" +
                            "Приклад: 1.09")
                    .build()
                    .showAndWait();
            return false;
        }
        return true;
    }
    @Override
    protected void objectToProcessBasedOnFields() {
        if (bType.isSelected()) {
            ((Fish) objectToProcess).setType(type.getText().trim().isEmpty() ? null : type.getText().trim());
        }

        if (bRow.isSelected()) {
            ((Fish) objectToProcess).setRow(row.getText().trim().isEmpty() ? null :
                    row.getText().trim());
        }

        if (bName.isSelected()) {
            ((Fish) objectToProcess).setName(name.getText().trim().isEmpty() ? null :
                    name.getText().trim());
        }

        if (bArsenal.isSelected()) {
            ((Fish) objectToProcess).setArea(area.getText().trim().isEmpty() ? null :
                    area.getText().trim());
        }

        if (bDescription.isSelected()) {
            ((Fish) objectToProcess).setDescription(description.getText().trim().isEmpty() ? null :
                    description.getText().trim());
        }

        if (bMax_weight.isSelected()) {
            ((Fish) objectToProcess).setMax_weight(max_weight.getText().trim().isEmpty() ? null :
                    new Double(max_weight.getText().trim()));
        }

        if (bOther.isSelected()) {
            ((Fish) objectToProcess).setOther(other.getText().trim().isEmpty() ? null : other.getText().trim());
        }
    }
    @Override
    protected String getDatabaseTableName() {
        return "fishs";
    }
}
