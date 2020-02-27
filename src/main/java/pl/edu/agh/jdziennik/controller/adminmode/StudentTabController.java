package pl.edu.agh.jdziennik.controller.adminmode;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;
import pl.edu.agh.jdziennik.wrapper.WrappedClass;
import pl.edu.agh.jdziennik.wrapper.WrappedParent;
import pl.edu.agh.jdziennik.wrapper.WrappedStudent;

import java.util.Optional;

/**
 * Controls student tab.
 * This class, together with view/adminmode/StudentTab.fxml, is used
 * to present a student tab in application window when application is
 * in admin mode. Student tab shows a table of all students.
 * Additional information about clicked-on student is also presented
 * in this tab. An option to add or delete a student is also given.
 */
public class StudentTabController extends Controller {

    @FXML
    private GridPane studentTab;

    @FXML
    private TableView<WrappedStudent> tableView;

    @FXML
    private TableColumn lpColumn;

    @FXML
    private TableColumn nameColumn;

    @FXML
    private TableColumn surnameColumn;

    @FXML
    private Button deleteStudentButton;

    @FXML
    private Button addStudentButton;

    @FXML
    private Label classLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label parentLabel;

    @FXML
    private ComboBox<WrappedClass> classComboBox;

    private ObservableList<WrappedStudent> studentObservableList = FXCollections.observableArrayList();
    private ObservableList<WrappedClass> teachingClassObservableList = FXCollections.observableArrayList();

    /**
     * Constructor for exclusive use by {@link  pl.edu.agh.jdziennik.controller.Controller the controller factory}.
     */
    public StudentTabController(App app, Stage stage) {
        super(app, stage);

        app.studentTabController = this;
    }

    private void disableRightSide() {
        deleteStudentButton.setDisable(true);
        phoneLabel.setText("");
        parentLabel.setText("");
        classLabel.setText("");
    }

    @FXML
    private void initialize() {
        disableRightSide();

        numerateLpColumn();
        nameColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedStudent, String>
                        ("name"));
        surnameColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedStudent, String>
                        ("surname"));

        TableView.TableViewSelectionModel selectionModel =
                tableView.getSelectionModel();

        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        selectionModel.getSelectedItems()
                .addListener(new ListChangeListener<WrappedStudent>() {
                    public void onChanged(Change change) {
                        ObservableList<WrappedStudent> selection =
                                change.getList();
                        if (selection.size() == 0)
                            disableRightSide();
                        else
                            presentStudent(selection.get(0));
                    }
                });

        loadStudents();
        loadClasses();
    }

    private void presentStudent(WrappedStudent ws) {
        deleteStudentButton.setDisable(false);

        classLabel.setText
                (accessor().wqueryClassOfStudent(ws).toString());

        WrappedParent parent = accessor().wqueryParentOfStudent(ws);

        phoneLabel.setText(parent.getObject().getPhoneNumber());
        parentLabel.setText(parent.toString());
    }

    private void loadStudents() {
        studentObservableList = accessor().wqueryAllStudents();
        tableView.setItems(studentObservableList);
    }

    private void loadClasses() {
        teachingClassObservableList.clear();
        teachingClassObservableList.addAll(accessor().wqueryAllClasses());
        classComboBox.setItems(teachingClassObservableList);
    }

    private void numerateLpColumn() {
        // adding numeration of first column
        lpColumn.setCellFactory(Controller.getAutonumerateCallback());
    }

    @FXML
    private void handleAddStudentAction(ActionEvent event) {
        studentTab.setDisable(true);

        Optional<WrappedStudent> maybeStudent = AddStudentController
                .interactivelyCreateStudent(getApp(), new Stage());

        maybeStudent.ifPresent(s -> tableView.getItems().add(s));

        studentTab.setDisable(false);
    }

    @FXML
    private void handleDeleteStudentAction(ActionEvent event) {
        for (WrappedStudent student : tableView.getSelectionModel().getSelectedItems()) {
            accessor().deleteObject(student.getObject());
            tableView.getItems().remove(student);
        }
    }

    @FXML
    private void handleClassChange(ActionEvent event) {
        studentObservableList.clear();
        studentObservableList.addAll(accessor().wqueryClassStudents(classComboBox.getValue()));
    }
}
