package pl.edu.agh.jdziennik.controller.adminmode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;
import pl.edu.agh.jdziennik.wrapper.WrappedClass;
import pl.edu.agh.jdziennik.wrapper.WrappedTeacher;

import java.util.Optional;

/**
 * Controls class creation dialog.
 * This class allows for interactive (via a dialog) creating of
 * teaching class and adding it to the database. This controller works
 * with resource view/adminmode/AddClassWindow.fxml and is
 * instantiated through it.
 */
public class AddClassController extends Controller {

    private WrappedClass newClass;

    @FXML
    private GridPane rootBox;

    @FXML
    private TextField idField;

    @FXML
    private Button addButton;

    @FXML
    private ComboBox<WrappedTeacher> teacherComboBox;

    /**
     * Constructor for exclusive use by {@link  pl.edu.agh.jdziennik.controller.Controller the controller factory}.
     */
    public AddClassController(App app, Stage stage) {
        super(app, stage);
    }

    /**
     * Causes a dialog to appear, which allows the user to create new teaching class.
     * The values inputted by the user are used to create a new
     * teaching class object, which gets added to the database.
     * Optional containing a wrapped version of this new object is
     * returned. In case of failure (i.e. user closes the dialog) an
     * empty optional is returned. Calling this method is the only
     * proper way for other code to use this class.
     *
     * @param app   Is the main application object.
     * @param stage Is an unused javafx stage for the dialog.
     */
    public static Optional<WrappedClass>
    interactivelyCreateClass(App app, Stage stage) {
        AddClassController controller = controllerFromFXML
                (app, stage, "view/adminmode/AddClassWindow.fxml");

        stage.showAndWait();

        return Optional.ofNullable(controller.newClass);
    }

    @FXML
    private void initialize() {
        getStage().setTitle("Jdziennik - dodawanie klasy");

        teacherComboBox.setItems(accessor().wqueryAllTeachers());
    }

    private boolean isInputValid() {
        // TODO check if class id is in right format...
        // see comments in AddStudentController.java
        return true;
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        if (idField.getText().length() == 0 | teacherComboBox.getSelectionModel().getSelectedItem() == null) {
            showAlertDialog(AlertType.WARNING,
                    "Prosze uzupelnic wszystkie pola");
            return;
        }
        if (!isInputValid()) return;

        newClass = accessor().createClass
                (idField.getText(),
                        teacherComboBox.getSelectionModel().getSelectedItem());

        rootBox.getScene().getWindow().hide();
    }
}
