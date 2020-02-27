package pl.edu.agh.jdziennik.controller.adminmode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;
import pl.edu.agh.jdziennik.wrapper.WrappedClass;
import pl.edu.agh.jdziennik.wrapper.WrappedClassSubject;
import pl.edu.agh.jdziennik.wrapper.WrappedSubject;
import pl.edu.agh.jdziennik.wrapper.WrappedTeacher;

import java.util.Optional;

/**
 * Controls Subject-to-Class assigning dialog.
 * This class allows for interactive (via a dialog) assigning of
 * a subject to a teaching class, which means creating a ClassSubject
 * object and adding it to the database. This controller works with
 * resource view/adminmode/AddClassSubjectWindow.fxml and is
 * instantiated through it.
 */
public class AddClassSubjectController extends Controller {

    private WrappedClassSubject newClassSubject;

    @FXML
    private GridPane rootBox;

    @FXML
    private ComboBox<WrappedClass> classComboBox;

    @FXML
    private ComboBox<WrappedSubject> subjectComboBox;

    @FXML
    private ComboBox<WrappedTeacher> teacherComboBox;

    @FXML
    private Button addClassButton;

    @FXML
    private Button addTeacherButton;

    @FXML
    private Button addButton;

    /**
     * Constructor for exclusive use by {@link  pl.edu.agh.jdziennik.controller.Controller the controller factory}.
     */
    public AddClassSubjectController(App app, Stage stage) {
        super(app, stage);
    }

    /**
     * Causes a dialog to appear, which allows the user to assign a subject to a class.
     * The subject, teching class and teacher selected by the user get
     * bound together in a new ClassSubject object, which gets added to
     * the database. Optional containing a wrapped version of this new
     * object is returned. In case of failure (i.e. user closes the
     * dialog) an empty optional is returned.
     * Calling this method is the only proper way for other code to use
     * this class.
     *
     * @param app   Is the main application object.
     * @param stage Is an unused javafx stage for the dialog.
     */
    public static Optional<WrappedClassSubject>
    interactivelyCreateClassSubject(App app, Stage stage) {
        AddClassSubjectController controller = controllerFromFXML
                (app, stage, "view/adminmode/AddClassSubjectWindow.fxml");

        stage.showAndWait();

        return Optional.ofNullable(controller.newClassSubject);
    }

    @FXML
    private void initialize() {
        getStage().setTitle("Jdziennik - dodawanie przedmiotu");

        classComboBox.setItems(accessor().wqueryAllClasses());
        teacherComboBox.setItems(accessor().wqueryAllTeachers());
        subjectComboBox.setItems(accessor().wqueryAllSubjects());
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
    private void handleAddTeacherAction(ActionEvent event) {
        rootBox.setDisable(true);

        AddTeacherController.interactivelyCreateTeacher(getApp(), new Stage()).ifPresent(c -> {
            teacherComboBox.getItems().add(c);
            teacherComboBox.getSelectionModel().select(c);
        });

        rootBox.setDisable(false);
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        if (classComboBox.getSelectionModel().getSelectedItem() == null
                || subjectComboBox.getSelectionModel().getSelectedItem() == null
                || teacherComboBox.getSelectionModel().getSelectedItem() == null) {
            showAlertDialog(AlertType.WARNING, "Prosze uzupelnic wszystkie pola");
            return;
        }

        if (accessor().wqueryClassSubject(classComboBox.getSelectionModel().getSelectedItem(), subjectComboBox.getSelectionModel().getSelectedItem(), teacherComboBox.getSelectionModel().getSelectedItem()) != null) {
            showAlertDialog(AlertType.WARNING, "Przedmiot o podanych danych już istnieje");
            return;
        }

        newClassSubject = accessor().createClassSubject(classComboBox.getSelectionModel().getSelectedItem(),
                subjectComboBox.getSelectionModel().getSelectedItem(),
                teacherComboBox.getSelectionModel().getSelectedItem());

        rootBox.getScene().getWindow().hide();
    }
}
