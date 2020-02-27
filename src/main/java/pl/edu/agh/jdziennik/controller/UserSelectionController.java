package pl.edu.agh.jdziennik.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.wrapper.WrappedTeacher;

import java.util.Optional;

/**
 * Controls user selection dialog.
 * This class, together with view/UserSelectionWindow.fxml, is used
 * to spawn a user selection dialog at the start of the application.
 * The user selection dialog allows either for selection of one of
 * the teachers from the database (in which case the application shall
 * continue in normal mode with selected teacher as current user) or
 * selection of admin mode, which has no notion of "current user".
 */
public class UserSelectionController extends Controller {
    private WrappedTeacher selectedUser;

    @FXML
    private ComboBox<WrappedTeacher> teacherComboBox;

    @FXML
    private Button chooseButton;

    @FXML
    private Button adminButton;

    /**
     * Constructor for exclusive use by {@link  pl.edu.agh.jdziennik.controller.Controller the controller factory}.
     */
    public UserSelectionController(App app, Stage stage) {
        super(app, stage);

        stage.setTitle("Jdziennik - wybór użytkownika");
    }

    /**
     * Causes a dialog to appear, which allows the user to select user and/or mode.
     * User is prompted with a dilog, that gives a choice of either
     * entering the admin mode or choosing one of the teachers from the
     * database as current user and enterimg normal mode. Should the
     * used choose admin mode, an empty optional is returned.
     * Otherwise, an optional containing a wrapped version of chosen
     * teacher is returned. If the dialog window is closed, an empty
     * optional is returned as well. Calling code can and should
     * change this behaviour by setting onCloseRequest of stage.
     *
     * @param app   Is the main application object.
     * @param stage Is an unused javafx stage for the dialog.
     */
    public static Optional<WrappedTeacher> interactivelySelectUser(App app, Stage stage) {
        UserSelectionController controller =
                controllerFromFXML(app, stage, "view/UserSelectionWindow.fxml");

        stage.showAndWait();

        return Optional.ofNullable(controller.selectedUser);
    }

    @FXML
    private void initialize() {
        chooseButton.setDisable(true);

        teacherComboBox.setItems(accessor().wqueryAllTeachers());
    }

    private WrappedTeacher getSelectedTeacher() {
        return teacherComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void handleValueSelectionAction(ActionEvent event) {
        chooseButton.setDisable(getSelectedTeacher() == null);
    }

    @FXML
    private void handleChooseAction(ActionEvent event) {
        selectedUser = getSelectedTeacher();
        getStage().hide();
    }

    @FXML
    private void handleAdminAction(ActionEvent event) {
        getStage().hide();
    }
}
