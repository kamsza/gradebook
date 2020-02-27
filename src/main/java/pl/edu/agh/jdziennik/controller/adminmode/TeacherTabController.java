package pl.edu.agh.jdziennik.controller.adminmode;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
 * Controls teacher tab.
 * This class, together with view/adminmode/TeacherTab.fxml, is used
 * to present a teacher tab in application window when application is
 * in admin mode. Teacher tab shows a table of all teachers. Classes
 * and subjects taught by the clicked-on teacher are also presented
 * in this tab. An option to add or delete a teacher is also given.
 */
public class TeacherTabController extends Controller {

    @FXML
    private GridPane teacherTab;

    @FXML
    private TableView<WrappedTeacher> tableView;

    @FXML
    private TableColumn lpColumn;

    @FXML
    private TableColumn nameColumn;

    @FXML
    private TableColumn surnameColumn;

    @FXML
    private TableView<WrappedClassSubject> subjectTableView;

    @FXML
    private TableColumn subjectColumn;

    @FXML
    private TableColumn classColumn;

    @FXML
    private Button addTeacherButton;

    @FXML
    private Button deleteTeacherButton;

    @FXML
    private Button deleteSubjectButton;

    @FXML
    private ComboBox<WrappedClass> classComboBox;

    @FXML
    private ComboBox<WrappedSubject> subjectComboBox;

    private ObservableList<WrappedClass> teachingClassObservableList = FXCollections.observableArrayList();
    private ObservableList<WrappedSubject> subjectObservableList = FXCollections.observableArrayList();
    private ObservableList<WrappedClassSubject> classSubjectObservableList = FXCollections.observableArrayList();

    /**
     * Constructor for exclusive use by {@link  pl.edu.agh.jdziennik.controller.Controller the controller factory}.
     */
    public TeacherTabController(App app, Stage stage) {
        super(app, stage);

        app.teacherTabController = this;
    }

    private void disableRightSide() {
        deleteTeacherButton.setDisable(true);
    }

    @FXML
    private void initialize() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        disableRightSide();

        numerateLpColumn();

        subjectTableView.setItems(classSubjectObservableList);

        nameColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedTeacher, String>("name"));

        surnameColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedTeacher, String>("surname"));

        subjectColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedClass, String>("subjectName"));

        classColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedClass, String>("classId"));


        ChangeListener<WrappedTeacher> teacherListener = (obs, oldValue, newValue) -> {
            if (newValue == null || newValue.getObject() == null)
                disableRightSide();
            else {
                deleteTeacherButton.setDisable(false);
                classSubjectObservableList.clear();
                classSubjectObservableList.addAll(accessor().wqueryClassSubjectsOfTeacher(newValue));
            }
        };

        ChangeListener<WrappedClassSubject> subjectListener = (obs, oldValue, newValue) -> {
            if (newValue == null || newValue.getObject() == null)
                deleteSubjectButton.setDisable(true);
            else
                deleteSubjectButton.setDisable(false);
        };

        tableView.getSelectionModel().selectedItemProperty().addListener(teacherListener);

        subjectTableView.getSelectionModel().selectedItemProperty().addListener(subjectListener);

        deleteSubjectButton.setDisable(true);

        loadTeachers();
        loadClasses();
        loadSubjects();
    }

    private void loadTeachers() {
        tableView.setItems(accessor().wqueryAllTeachers());
    }

    private void loadClasses() {
        teachingClassObservableList.clear();
        teachingClassObservableList.addAll(accessor().wqueryAllClasses());
        classComboBox.setItems(teachingClassObservableList);
    }

    private void loadSubjects() {
        subjectObservableList.clear();
        subjectObservableList.addAll(accessor().wqueryAllSubjects());
        subjectComboBox.setItems(subjectObservableList);
    }


    private void numerateLpColumn() {
        // adding numeration of first column
        lpColumn.setCellFactory(Controller.getAutonumerateCallback());
    }

    @FXML
    private void handleAddTeacherAction(ActionEvent event) {
        teacherTab.setDisable(true);

        Optional<WrappedTeacher> maybeTeacher = AddTeacherController
                .interactivelyCreateTeacher(getApp(), new Stage());

        maybeTeacher.ifPresent(s -> tableView.getItems().add(s));

        // TODO disable entire window, not just tab
        teacherTab.setDisable(false);
    }

    @FXML
    private void handleDeleteTeacherAction(ActionEvent event) {
        for (WrappedTeacher teacher : tableView.getSelectionModel().getSelectedItems()) {
            if (!accessor().wqueryClassesTutoredByTeacher(teacher).isEmpty()
                    | !accessor().wqueryClassesOfTeacher(teacher).isEmpty()) {
                showAlertDialog(AlertType.WARNING,
                        "Nie mozna usunac nauczyciela");
                return;
            }
            accessor().deleteObject(teacher.getObject());
            tableView.getItems().remove(teacher);
        }
    }

    @FXML
    private void handleClassChange(ActionEvent event) {
        if (tableView.getSelectionModel().getSelectedItem() == null || classComboBox.getSelectionModel().getSelectedItem() == null)
            return;

        classSubjectObservableList.clear();
        classSubjectObservableList.addAll(accessor().wqueryClassSubjectsOfTeacherAndClass(tableView.getSelectionModel().getSelectedItem(), classComboBox.getSelectionModel().getSelectedItem()));

        subjectComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSubjectChange(ActionEvent event) {
        if (tableView.getSelectionModel().getSelectedItem() == null || subjectComboBox.getSelectionModel().getSelectedItem() == null)
            return;

        classSubjectObservableList.clear();
        classSubjectObservableList.addAll(accessor().wqueryClassSubjectsOfTeacherAndSubject(tableView.getSelectionModel().getSelectedItem(), subjectComboBox.getSelectionModel().getSelectedItem()));

        classComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleAddSubjectAction(ActionEvent event) {
        Parent root = teacherTab.getScene().getRoot();
        root.setDisable(true);

        AddClassSubjectController
                .interactivelyCreateClassSubject(getApp(), new Stage())
                .ifPresent(c -> subjectTableView.getItems().add(c));

        root.setDisable(false);
    }

    @FXML
    private void handleDeleteSubjectAction(ActionEvent event) {
        WrappedClassSubject cs = subjectTableView.getSelectionModel().getSelectedItem();

        if (cs == null) return;

        accessor().deleteObject(cs.getObject());

        subjectTableView.getItems().remove(cs);
    }
}
