package base;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import main_form.Controller;
import models.*;
import server.DatabaseWorker;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;

/**
 * Created by shrralis on 3/13/17.
 */
@SuppressWarnings("unchecked")
public abstract class DataFormController<T extends Model> implements DataFormControllerInterface {
    private int iNumOfSelectedForSearch = 0;
    private OnMouseClickListener okClickListener = null;
    private OnMouseClickListener cancelClickListener = null;
    private Type type = Type.Add;
    protected Stage primaryStage = null;
    protected T objectToProcess = null;

    @FXML
    private void onMouseOkClick() {
        boolean success = false;

        switch (type) {
            case Add:
                success = addObjectToDatabase();
                break;
            case Edit:
                success = editObjectInDatabase();
                break;
        }

        if ((type == Type.Search ^ success) && okClickListener != null) {
            okClickListener.onMouseClick();
        }

        if (success) {
            primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }
    @FXML
    private void onMouseCancelClick() {
        if (cancelClickListener != null) {
            cancelClickListener.onMouseClick();
        }
        primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
    @FXML
    private void onCheckBoxEnablerSelect(ActionEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();

        if (checkBox.isSelected()) {
            iNumOfSelectedForSearch++;
        } else {
            if (iNumOfSelectedForSearch > 0) {
                iNumOfSelectedForSearch--;
            }
        }
        setFieldEnabledByCheckBox(checkBox);
    }

    @Override
    public final void setType(Type type) {
        this.type = type;
    }
    @Override
    public final void setOnMouseOkClickListener(OnMouseClickListener listener) {
        okClickListener = listener;
    }
    @Override
    public final void setOnMouseCancelClickListener(OnMouseClickListener listener) {
        cancelClickListener = listener;
    }
    @Override
    public void setPrimaryStage(Stage stage) {
        stage.initStyle(StageStyle.UTILITY);
        stage.addEventHandler(
                WindowEvent.WINDOW_SHOWN,
                event -> {
                    double height = stage.getHeight();
                    double width = stage.getWidth();
                    String title = null;

                    switch (type) {
                        case Add:
                            title = "Додавання";
                            break;
                        case Edit:
                            title = "Редагування";
                            break;
                        case Search:
                            width += 28;
                            title = "Пошук";
                            break;
                    }
                    stage.setMinHeight(height);
                    stage.setMaxHeight(height);
                    stage.setMinWidth(width);
                    stage.setMaxWidth(width);
                    stage.setTitle(title);
                }
        );
        stage.addEventHandler(
                WindowEvent.WINDOW_SHOWING,
                event-> {
                    setAllFieldsEnabled(type == Type.Add || type == Type.Edit);

                    if (type == Type.Edit) {
                        initializeFromObjectToProcess();
                    }
                }
        );

        primaryStage = stage;
    }
    @Override
    public <P extends Owner> void setObjectToProcess(P obj) {
        objectToProcess = (T) obj;
    }
    @Override
    public final void addObjectToTableView(TableView dest) {
        if (dest != null && objectToProcess != null) {
            dest.getItems().add(objectToProcess);
        } else {
            throw new NullPointerException("Table or your object is null!");
        }
    }
    @Override
    public final void editObjectInTableView(TableView tableView) {
        if (tableView != null && objectToProcess != null) {
            if (editObjectInDatabase()) {
                tableView.getItems().set(
                        tableView.getItems().indexOf(tableView.getSelectionModel().getSelectedItem()),
                        objectToProcess
                );
                tableView.refresh();
            }
        } else {
            throw new NullPointerException("Table or your object is null!");
        }
    }
    @Override
    public final void search(Controller mainController, TableView dest) {
        if (areTheFieldsValidForSearch()) {
            objectToProcessBasedOnFields();

            try {
                mainController.loadDataToTableFromDatabase(dest, getParamsForSearch());
            } catch (IllegalAccessException ignored) {}
        }
    }

    protected boolean isAnyTextFieldEmpty() {
        for (Field field : getClass().getDeclaredFields()) {
            try {
                String fieldDaoName = "";
                boolean empty = false;

                if (field.getType() == TextField.class) {
                    fieldDaoName = ((TextField) field.get(this)).getPromptText();
                    empty = ((TextField) field.get(this)).getText().trim().equalsIgnoreCase("");
                } else if (field.getType() == DatePicker.class) {
                    fieldDaoName = ((DatePicker) field.get(this)).getPromptText();
                    empty = ((DatePicker) field.get(this)).getValue() == null;
                }

                if (empty) {
                    AlertsBuilder.start()
                            .setAlertType(Alert.AlertType.WARNING)
                            .setTitle("Помилка")
                            .setHeaderText("Помилка вводу")
                            .setContentText("Ви залишили поле " + fieldDaoName + " порожнім!")
                            .buildOnFront(primaryStage)
                            .showAndWait();
                    return true;
                }
            } catch (IllegalAccessException ignored) {}
        }
        return false;
    }

    private boolean areAllSelectedFieldsEmpty() {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.getName().matches("^b(\\d|\\D)+$") && field.getType() == CheckBox.class) {
                try {
                    if (((CheckBox) field.get(this)).isSelected()) {
                        String neededFieldName = field.getName().substring(1);

                        for (Field neededField : getClass().getDeclaredFields()) {
                            if (neededField.getName().equalsIgnoreCase(neededFieldName)) {
                                boolean empty = true;
                                String daoFieldName = "";

                                if (neededField.getType() == TextField.class) {
                                    empty = ((TextField) neededField.get(this)).getText().trim().isEmpty();
                                    daoFieldName = ((TextField) neededField.get(this)).getPromptText();
                                } else if (neededField.getType() == DatePicker.class) {
                                    empty = ((DatePicker) neededField.get(this)).getValue() == null;
                                    daoFieldName = ((DatePicker) neededField.get(this)).getPromptText();
                                } else if (neededField.getType() == ComboBox.class) {
                                    empty = ((ComboBox) neededField.get(this)).getValue() == null;
                                    daoFieldName = ((ComboBox) neededField.get(this)).getPromptText();
                                    String toRemove = "Оберіть ";
                                    daoFieldName = daoFieldName.substring(daoFieldName.indexOf(toRemove) + toRemove.length());
                                }

                                if (!empty) {
                                    return false;
                                } else {
                                    AlertsBuilder.start()
                                            .setAlertType(Alert.AlertType.WARNING)
                                            .setTitle("Помилка")
                                            .setHeaderText("Помилка пошуку")
                                            .setContentText("Обране для пошуку поле " + daoFieldName + " порожнє!");
                                }
                            }
                        }
                    }
                } catch (IllegalAccessException ignored) {}
            }
        }
        return true;
    }

    private boolean isAnyFieldSelectedForSearch() {
        if (iNumOfSelectedForSearch > 0) {
            return true;
        }
        AlertsBuilder.start()
                .setAlertType(Alert.AlertType.WARNING)
                .setTitle("Помилка")
                .setHeaderText("Помилка пошуку")
                .setContentText("Ви не обрали жодного поля для пошуку!")
                .build()
                .showAndWait();
        return false;
    }

    private boolean areTheFieldsValidForSearch() {
        return isAnyFieldSelectedForSearch() || !areAllSelectedFieldsEmpty();
    }

    protected boolean addObjectToDatabase() {
        if (areTheFieldsValidForAdding()) {
            objectToProcessBasedOnFields();

            ServerResult result = DatabaseWorker.processQuery(
                    ServerQuery.create(
                            getDatabaseTableName(),
                            "add",
                            objectToProcess,
                            null
                    )
            );

            return result != null && result.getResult() == 0;
        }
        return false;
    }
    
    protected boolean editObjectInDatabase() {
        if (areTheFieldsValidForAdding()) {
            objectToProcessBasedOnFields();

            HashMap<String, Object> params = new HashMap<>();

            params.put("id", ((Owner) objectToProcess).getId());

            ServerResult result = DatabaseWorker.processQuery(
                    ServerQuery.create(
                            getDatabaseTableName(),
                            "edit",
                            objectToProcess,
                            params
                    )
            );

            return result != null && result.getResult() == 0;
        }
        return false;
    }

    private HashMap<String, Object> getParamsForSearch() throws IllegalAccessException {
        HashMap<String, Object> paramsForSearch = new HashMap<>();

        for (Field field : getClass().getDeclaredFields()) {
            if (field.getName().matches("^b(\\s|\\S)+$")) {
                if (field.getType() == CheckBox.class) {
                    if (((CheckBox) field.get(this)).isSelected()) {
                        String neededFieldName = field.getName().substring(1).toLowerCase();

                        for (Field field1 : getClass().getDeclaredFields()) {
                            if (field1.getName().equalsIgnoreCase(neededFieldName)) {
                                if (field1.getType() == ComboBox.class) {
                                    if (Owner.class.isAssignableFrom(
                                            ((Class<?>)
                                                    ((ParameterizedType) field1.getGenericType()).getActualTypeArguments()[0]))
                                            ) {
                                        paramsForSearch.put(
                                                neededFieldName,
                                                ((Owner) ((ComboBox) field1.get(this)).getSelectionModel().getSelectedItem())
                                                        .getId()
                                        );
                                    } else {
                                        paramsForSearch.put(
                                                neededFieldName,
                                                ((ComboBox) field1.get(this)).getSelectionModel().getSelectedItem()
                                        );
                                    }

                                } else if (field1.getType() == DatePicker.class) {
                                    paramsForSearch.put(
                                            neededFieldName,
                                            DateWorker.convertDateToString((DatePicker) field1.get(this))
                                    );
                                } else {
                                    paramsForSearch.put(
                                            neededFieldName,
                                            ((TextField) field1.get(this)).getText()
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
        return paramsForSearch;
    }

    private void setAllFieldsEnabled(boolean enabled) {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.getName().matches("^b(\\d|\\D)+$")
                    && field.getType() == CheckBox.class) {
                try {
                    CheckBox checkBox = (CheckBox) field.get(this);

                    checkBox.setSelected(enabled);
                    setFieldEnabledByCheckBox(checkBox);
                } catch (IllegalAccessException ignored) {}
            }
        }
    }

    private void setFieldEnabledByCheckBox(CheckBox checkBox) {
        boolean enabled = false;
        String name = "";

        try {
            for (Field field : getClass().getDeclaredFields()) {
                if (field.getType() == CheckBox.class && field.get(this) == checkBox) {
                    enabled = checkBox.isSelected();
                    name = field.getName().substring(1);
                    break;
                }
            }

            for (Field field : getClass().getDeclaredFields()) {
                if ((field.getType() == TextField.class
                        || field.getType() == ComboBox.class
                        || field.getType() == DatePicker.class)
                        && field.getName().equalsIgnoreCase(name)) {
                    ((Parent) field.get(this)).setDisable(!enabled);
                    return;
                }
            }
        } catch (IllegalAccessException ignored) {}
    }

    private void initializeFromObjectToProcess() {
        for (Field field : objectToProcess.getClass().getFields()) {
            for (Field field1 : getClass().getDeclaredFields()) {
                if (field.getName().equalsIgnoreCase(field1.getName())) {
                    try {
                        Object value = field.get(objectToProcess);

                        if (value != null) {
                            if (field1.getType().equals(TextField.class)) {
                                ((TextField) field1.get(this)).setText(value.toString());
                            } else if (field1.getType().equals(DatePicker.class)) {
                                LocalDate date;
                                if (value instanceof Date) {
                                    date = new java.util.Date(((Date) value).getTime())
                                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                } else {
                                    date = ((java.util.Date) value)
                                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                }
                                ((DatePicker) field1.get(this)).setValue(date);
                                break;
                            } else if (field1.getType().equals(ComboBox.class)) {
                                ((ComboBox) field1.get(this)).getSelectionModel().select(value);
                                break;
                            }
                        } else {
                            System.out.println(field1.getName());
                        }
                    } catch (IllegalAccessException ignored) {}
                    break;
                }
            }
        }
    }

    protected abstract boolean areTheFieldsValidForAdding();
    protected abstract void objectToProcessBasedOnFields();
    protected abstract String getDatabaseTableName();
}
