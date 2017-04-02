package base;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by shrralis on 3/13/17.
 */
public class AlertsBuilder {
    private Alert alert = new Alert(Alert.AlertType.NONE);
    private Stage dialogStage = new Stage(StageStyle.UTILITY);

    private AlertsBuilder() {}

    public static AlertsBuilder start() {
        return new AlertsBuilder();
    }

    public AlertsBuilder setAlertType(Alert.AlertType type) {
        alert.setAlertType(type);
        return this;
    }

    public AlertsBuilder setTitle(String title) {
        alert.setTitle(title);
        return this;
    }

    public AlertsBuilder setHeaderText(String text) {
        alert.setHeaderText(text);
        return this;
    }

    public AlertsBuilder setContentText(String text) {
        alert.setContentText(text);
        return this;
    }

    public Alert buildOnFront(Stage stage) {
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(stage.getScene().getWindow());
        return alert;
    }

    public Stage build() {
        DialogPane root = alert.getDialogPane();

        for (ButtonType buttonType : root.getButtonTypes()) {
            ButtonBase button = (ButtonBase) root.lookupButton(buttonType);
            button.setOnAction(evt -> {
                root.setUserData(buttonType);
                dialogStage.close();
            });
        }
        root.getScene().setRoot(new Group());
        root.setPadding(new Insets(10, 0, 10, 0));

        Scene scene = new Scene(root);

        dialogStage.setTitle(alert.getTitle());
        dialogStage.setScene(scene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setAlwaysOnTop(true);
        dialogStage.setResizable(false);
        return dialogStage;
    }
}
