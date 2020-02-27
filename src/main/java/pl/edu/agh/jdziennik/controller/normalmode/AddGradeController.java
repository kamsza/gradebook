package pl.edu.agh.jdziennik.controller.normalmode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;
import pl.edu.agh.jdziennik.wrapper.WrappedClassSubject;
import pl.edu.agh.jdziennik.wrapper.WrappedGrade;
import pl.edu.agh.jdziennik.wrapper.WrappedGradeType;
import pl.edu.agh.jdziennik.wrapper.WrappedGradeValue;
import pl.edu.agh.jdziennik.wrapper.WrappedStudentWithSubject;

import java.util.Optional;


public class AddGradeController extends Controller {

    @FXML
    private GridPane rootPane;
    @FXML
    private Label studentLabel;
    @FXML
    private Label subjectLabel;
    @FXML
    private ComboBox<WrappedGradeValue> gradeComboBox;
    @FXML
    private ComboBox<WrappedGradeType> gradeTypeComboBox;
    @FXML
    private TextArea commentTextArea;

    private WrappedGrade newGrade;
    private WrappedStudentWithSubject student;

    private ObservableList<WrappedGradeValue> gradeValues = FXCollections.observableArrayList(WrappedGradeValue.values());
    private ObservableList<WrappedGradeType> gradeTypes = FXCollections.observableArrayList(accessor().wqueryGradeTypes(getApp().getUser()));

    public AddGradeController(App app, Stage stage) {
        super(app, stage);
    }

    /**
     * Static method, that can be called to show AddGradeWindow
     *
     * @param student WrappedStudentWithSubject object, for which user wants to add grade
     * @return grade that was added
     */
    public static Optional<WrappedGrade> newAddGradeWindow(App app, Stage stage, WrappedStudentWithSubject student) {
        if (student.getSubject() == null) {
            Controller.showAlertDialog(AlertType.WARNING, "Prosze wybrac przedmiot");
            return Optional.empty();
        }

        AddGradeController controller = controllerFromFXML(app, stage, "view/normalmode/AddGradeWindow.fxml");
        controller.student = student;
        controller.studentLabel.setText(student.getObject().getSurname() + "  " + student.getObject().getName());
        controller.subjectLabel.setText(student.getSubject().getName());

        stage.showAndWait();

        return Optional.ofNullable(controller.newGrade);
    }

    @FXML
    private void initialize() {
        getStage().setTitle("Jdziennik - dodawanie oceny");

        gradeComboBox.setItems(gradeValues);

        gradeTypeComboBox.setItems(gradeTypes);
    }

    /**
     * After pressing the button adds new grade to database and closes AddGradeWindow
     */
    @FXML
    public void handleAddGradeAction(ActionEvent actionEvent) {
        String comment = commentTextArea.getText();

        WrappedClassSubject currentClassSubject = accessor().wqueryClassSubject(student, getApp().getUser());

        newGrade = accessor().createGrade(student.getObject(), currentClassSubject.getObject(), gradeComboBox.getValue().getObject(), gradeTypeComboBox.getValue().getObject(), comment);

        rootPane.getScene().getWindow().hide();
    }
}
