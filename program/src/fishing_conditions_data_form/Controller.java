package fishing_conditions_data_form;

import base.AlertsBuilder;
import base.DataFormController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import models.DateWorker;
import models.FishingCondition;

import java.util.Calendar;

/**
 * Created by shrralis on 3/13/17.
 */
@SuppressWarnings("unused")
public class Controller extends DataFormController {
    {
        objectToProcess = new FishingCondition();
    }

    @FXML public TextField name;
    @FXML public TextField general_description;
    @FXML public TextField time;
    @FXML public TextField place;
    @FXML public TextField tackle;
    @FXML public TextField other;
    @FXML public CheckBox bName;
    @FXML public CheckBox bGeneral_description;
    @FXML public CheckBox bTime;
    @FXML public CheckBox bPlace;
    @FXML public CheckBox bTackle;
    @FXML public CheckBox bOther;

    @Override
    protected boolean isAnyTextFieldEmpty() {
        boolean empty = false;
        String fieldDaoName = "";

        if (name.getText().equalsIgnoreCase("")) {
            empty = true;
            fieldDaoName = name.getPromptText();
        } else if (general_description.getText().equalsIgnoreCase("")) {
            empty = true;
            fieldDaoName = general_description.getPromptText();
        } else if (time.getText().equalsIgnoreCase("")) {
            empty = true;
            fieldDaoName = time.getPromptText();
        } else if (place.getText().equalsIgnoreCase("")) {
            empty = true;
            fieldDaoName = place.getPromptText();
        } else if (tackle.getText().equalsIgnoreCase("")) {
            empty = true;
            fieldDaoName = tackle.getPromptText();
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

        if (!time.getText().trim().matches("^\\d{2}:\\d{2}:\\d{2}$")) {
            AlertsBuilder.start()
                    .setAlertType(Alert.AlertType.WARNING)
                    .setTitle("Помилка")
                    .setHeaderText("Помилкка вводу")
                    .setContentText("Невірний формат поля Час для рибалки!\n" +
                            "Приклад: 00:12:47 (гг:хв:сс)")
                    .buildOnFront(primaryStage)
                    .showAndWait();
            return false;
        }
        return true;
    }
    @Override
    protected void objectToProcessBasedOnFields() {
        if (bName.isSelected()) {
            ((FishingCondition) objectToProcess).setName(name.getText().trim().isEmpty() ? null : name.getText().trim());
        }

        if (bGeneral_description.isSelected()) {
            ((FishingCondition) objectToProcess).setGeneral_description(general_description.getText().trim().isEmpty() ?
                    null : general_description.getText().trim());
        }

        if (bTime.isSelected()) {
            ((FishingCondition) objectToProcess).setTime(DateWorker.convertToTime(time));
        }

        if (bPlace.isSelected()) {
            ((FishingCondition) objectToProcess).setPlace(place.getText().trim().isEmpty() ? null :
                    place.getText().trim());
        }

        if (bTackle.isSelected()) {
            ((FishingCondition) objectToProcess).setTackle(tackle.getText().trim().isEmpty() ? null :
                    tackle.getText().trim());
        }

        if (bOther.isSelected()) {
            ((FishingCondition) objectToProcess).setOther(other.getText().trim().isEmpty() ? null : other.getText().trim());
        }
    }
    @Override
    protected String getDatabaseTableName() {
        return "fishingConditions";
    }
}
