package main_form;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.DatabaseWorker;

import java.io.IOException;

public class Main extends Application {
    private final FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_form/main.fxml"));

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        loader.load();

        if (DatabaseWorker.openConnection()) {
            drawForm(primaryStage);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Критична помилка");
            alert.setHeaderText("Помилка з'єднання");
            alert.setContentText("З'єднання з сервером неможливе.\n" +
                    "Всі деталі в консолі.\n" +
                    "Програма буде завершена.");
            alert.showAndWait();
            primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }
    @SuppressWarnings("unchecked")
    private void drawForm(Stage primaryStage) {
        Parent root = loader.getRoot();

        primaryStage.setTitle("Риболовля");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(root.minWidth(-1));
        primaryStage.setMinHeight(root.minHeight(-1) + 50);
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWING, event -> ((Controller) loader.getController()).setupAllTables());
        primaryStage.setOnCloseRequest(event -> DatabaseWorker.closeConnection());
        primaryStage.show();
    }
}
