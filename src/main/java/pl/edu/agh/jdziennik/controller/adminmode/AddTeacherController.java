package pl.edu.agh.jdziennik.controller.adminmode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;
import pl.edu.agh.jdziennik.wrapper.WrappedTeacher;

import java.util.Optional;

/**
 * Controls teacher creation dialog.
 * This class allows for interactive (via a dialog) creating of
 * teacher and adding them to the database. This controller works with
 * resource view/adminmode/AddTeacherWindow.fxml and is instantiated
 * through it.
 */
public class AddTeacherController extends Controller {

    private WrappedTeacher newTeacher;

    @FXML
    private GridPane rootBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private Button addButton;

    /**
     * Constructor for exclusive use by {@link  pl.edu.agh.jdziennik.controller.Controller the controller factory}.
     */
    public AddTeacherController(App app, Stage stage) {
        super(app, stage);
    }

    /**
     * Causes a dialog to appear, which allows the user to create new teacher.
     * The values inputted by the user are used to create a new
     * teacher object, which gets added to the database. Optional
     * containing a wrapped version of this new object is returned.
     * In case of failure (i.e. user closes the dialog) an empty
     * optional is returned. Calling this method is the only proper way
     * for other code to use this class.
     *
     * @param app   Is the main application object.
     * @param stage Is an unused javafx stage for the dialog.
     */
    public static Optional<WrappedTeacher>
    interactivelyCreateTeacher(App app, Stage stage) {
        AddTeacherController controller = controllerFromFXML
                (app, stage, "view/adminmode/AddTeacherWindow.fxml");

        stage.showAndWait();

        return Optional.ofNullable(controller.newTeacher);
    }

    @FXML
    private void initialize() {
        getStage().setTitle("Jdziennik - dodawanie nauczyciela");
    }

    private boolean isInputValid() {
        // TODO
        return true;
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        if (nameField.getText().length() == 0 | surnameField.getText().length() == 0) {
            showAlertDialog(AlertType.WARNING,
                    "Prosze uzupelnic wszystkie pola");
            return;
        }
        if (!isInputValid()) return;

        newTeacher = accessor().createTeacher
                (nameField.getText(), surnameField.getText());

        rootBox.getScene().getWindow().hide();
    }
}
