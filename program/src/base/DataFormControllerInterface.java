package base;

import javafx.scene.control.TableView;
import javafx.stage.Stage;
import main_form.Controller;
import models.Owner;

/**
 * Created by shrralis on 3/13/17.
 */
public interface DataFormControllerInterface {
    enum Type {
        Add,
        Edit,
        Search
    }

    void setType(Type type);
    void setOnMouseOkClickListener(OnMouseClickListener listener);
    void setOnMouseCancelClickListener(OnMouseClickListener listener);
    void setPrimaryStage(Stage stage);
    <P extends Owner> void setObjectToProcess(P obj);
    void addObjectToTableView(TableView dest);
    void editObjectInTableView(TableView tableView);
    void search(Controller mainController, TableView dest);
}
