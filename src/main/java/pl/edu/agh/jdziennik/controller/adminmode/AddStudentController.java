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
import pl.edu.agh.jdziennik.wrapper.WrappedParent;
import pl.edu.agh.jdziennik.wrapper.WrappedStudent;

import java.util.Optional;

/**
 * Controls student creation dialog.
 * This class allows for interactive (via a dialog) creating of
 * student and adding them to the database. This controller works with
 * resource view/adminmode/AddStudentWindow.fxml and is instantiated
 * through it.
 */
public class AddStudentController extends Controller {

    private WrappedStudent newStudent;

    @FXML
    private GridPane rootBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField PESELField;

    @FXML
    private Button addClassButton;

    @FXML
    private Button addParentButton;

    @FXML
    private Button addButton;

    @FXML
    private ComboBox<WrappedClass> classComboBox;

    @FXML
    private ComboBox<WrappedParent> parentComboBox;

    /**
     * Constructor for exclusive use by {@link  pl.edu.agh.jdziennik.controller.Controller the controller factory}.
     */
    public AddStudentController(App app, Stage stage) {
        super(app, stage);
    }

    /**
     * Causes a dialog to appear, which allows the user to create new student.
     * The values inputted by the user are used to create a new
     * student object, which gets added to the database. Optional
     * containing a wrapped version of this new object is returned.
     * In case of failure (i.e. user closes the dialog) an empty
     * optional is returned. Calling this method is the only proper way
     * for other code to use this class.
     *
     * @param app   Is the main application object.
     * @param stage Is an unused javafx stage for the dialog.
     */
    public static Optional<WrappedStudent>
    interactivelyCreateStudent(App app, Stage stage) {
        AddStudentController controller = controllerFromFXML
                (app, stage, "view/adminmode/AddStudentWindow.fxml");

        stage.showAndWait();

        return Optional.ofNullable(controller.newStudent);
    }

    @FXML
    private void initialize() {
        getStage().setTitle("Jdziennik - dodawanie ucznia");

        classComboBox.setItems(accessor().wqueryAllClasses());
        parentComboBox.setItems(accessor().wqueryAllParents());
    }

    @FXML
    private void handleAddClassAction(ActionEvent event) {
        rootBox.setDisable(true);

        AddClassController.interactivelyCreateClass
                (getApp(), new Stage()).ifPresent(c -> {
            classComboBox.getItems().add(c);
            classComboBox.getSelectionModel().select(c);
        });

        rootBox.setDisable(false);
    }

    @FXML
    private void handleAddParentAction(ActionEvent event) {
        rootBox.setDisable(true);

        AddParentController.interactivelyCreateParent(getApp(), new Stage()).ifPresent(c -> {
            parentComboBox.getItems().add(c);
            parentComboBox.getSelectionModel().select(c);
        });

        rootBox.setDisable(false);
    }

    private boolean isInputValid() {
        // TODO check if text fields are not empty, if PESEL field
        // is of required length, if values in combo boxes are
        // selected...
        // We have yet to decide where input validation should be
        // done - there're already enough things happening in this
        // class. WrappingAccessor is also not the right place
        // for this... It might be that we'll just create a Validator
        // class with only static methods... It might not be the
        // most object-oriented approach, but it shall work the best
        return true;
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        if (nameField.getText().length() == 0 | surnameField.getText().length() == 0
                | PESELField.getText().length() == 0 | parentComboBox.getSelectionModel().getSelectedItem() == null) {
            showAlertDialog(AlertType.WARNING,
                    "Prosze uzupelnic wszystkie pola");
            return;
        }
        if (!isInputValid()) return;

        newStudent = accessor().createStudent
                (classComboBox.getSelectionModel().getSelectedItem(),
                        nameField.getText(), surnameField.getText(),
                        PESELField.getText(),
                        parentComboBox.getSelectionModel().getSelectedItem());

        rootBox.getScene().getWindow().hide();
    }
}
