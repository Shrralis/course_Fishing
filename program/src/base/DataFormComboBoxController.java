package base;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Owner;
import models.ServerQuery;
import models.ServerResult;
import server.DatabaseWorker;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * Created by shrralis on 3/13/17.
 */
@SuppressWarnings("unchecked")
public abstract class DataFormComboBoxController extends DataFormController implements DataFormComboBoxInterface {
    @Override
    public void setPrimaryStage(Stage stage) {
        stage.addEventHandler(WindowEvent.WINDOW_SHOWING, event -> {
            loadData();
        });
        super.setPrimaryStage(stage);
    }
    @Override
    public void loadData() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getType().equals(ComboBox.class)) {
                Class<?> type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

                if (Owner.class.isAssignableFrom(type)) {
                    System.out.println(type.getClass().getSimpleName());
                    setComboBox((Class<? extends Owner>) type);
                }
            }
        }
    }

    protected boolean isAnyComboBoxEmpty() {
        for (Field field : getClass().getDeclaredFields()) {
            try {
                String fieldDaoName = "";
                boolean empty = false;

                if (field.getType().equals(ComboBox.class)) {
                    fieldDaoName = ((ComboBox) field.get(this)).getPromptText();
                    empty = ((ComboBox) field.get(this)).getValue() == null;
                }

                if (empty) {
                    AlertsBuilder.start()
                            .setAlertType(Alert.AlertType.WARNING)
                            .setTitle("Помилка")
                            .setHeaderText("Помилка вводу")
                            .setContentText("Ви залишили поле \"" + fieldDaoName + "\" порожнім!")
                            .buildOnFront(primaryStage)
                            .showAndWait();
                    return true;
                }
            } catch (IllegalAccessException ignored) {}
        }
        return false;
    }

    private <T extends Owner> void setComboBox(Class<T> clazz) {
        String tableName = clazz.getSimpleName().toLowerCase();

        if (tableName.matches("^(\\D|\\d)+y$")) {
            tableName = tableName.substring(0, tableName.lastIndexOf('y')) + "ies";
        } else {
            tableName += "s";
        }

        ServerQuery query = ServerQuery.create(tableName, "get", null, null);
        ObservableList<T> list = FXCollections.observableArrayList();

        try {
            if (DatabaseWorker.openConnection()) {
                ServerResult result = DatabaseWorker.processQuery(query);
                
                if (result != null && result.getResult() == 0) {
                    if (result.getObjects() != null) {
                        list.addAll(result.getObjects());
                    }
                } else {
                    throw new RuntimeException("Result is not success!");
                }

                for (Field field : this.getClass().getDeclaredFields()) {
                    if (field.getName().equalsIgnoreCase(clazz.getSimpleName())) {
                        ((ComboBox) field.get(this)).setItems(list);
                        return;
                    }
                }
            } else {
                throw new NullPointerException("Database connection problem!");
            }
        } catch (IllegalAccessException ignored) {}
    }
}
