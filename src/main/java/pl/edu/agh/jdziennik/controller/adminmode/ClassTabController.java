package pl.edu.agh.jdziennik.controller.adminmode;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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

/**
 * Controls class tab.
 * This class, together with view/adminmode/ClassTab.fxml, is used
 * to present a class tab in application window when application is
 * in admin mode. Class tab shows a table of all teaching classes with
 * their tutors. Subjects taught to clicked-on class along with
 * teachers are also presented in this tab. An option to add or
 * delete a class or assign a new subject to a class is also given.
 */
public class ClassTabController extends Controller {

    @FXML
    private GridPane classTab;

    @FXML
    private TableView<WrappedClass> classTableView;

    @FXML
    private TableColumn lpColumn;

    @FXML
    private TableColumn classColumn;

    @FXML
    private TableColumn tutorColumn;

    @FXML
    private TableView<WrappedClassSubject> subjectTableView;

    @FXML
    private TableColumn subjectColumn;

    @FXML
    private TableColumn teacherColumn;

    @FXML
    private Button addClassButton;

    @FXML
    private Button addSubjectButton;

    @FXML
    private Button deleteClassButton;

    /**
     * Constructor for exclusive use by {@link  pl.edu.agh.jdziennik.controller.Controller the controller factory}.
     */
    public ClassTabController(App app, Stage stage) {
        super(app, stage);

        app.classTabController = this;
    }

    private void disableRightSide() {
        deleteClassButton.setDisable(true);
    }

    @FXML
    private void initialize() {
        disableRightSide();

        numerateLpColumn();
        classColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedClass, String>("id"));

        tutorColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedClass, String>("tutorName"));

        subjectColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedClassSubject, String>("subjectName"));

        teacherColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedClassSubject, String>("teacherName"));

        TableView.TableViewSelectionModel selectionModel =
                classTableView.getSelectionModel();

        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        selectionModel.getSelectedItems()
                .addListener(new ListChangeListener<WrappedClass>() {
                    public void onChanged(Change change) {
                        ObservableList<WrappedClass> selection =
                                change.getList();
                        if (selection.size() == 0)
                            disableRightSide();
                        else
                            presentClass(selection.get(0));
                    }
                });

        loadClasses();
    }

    private void presentClass(WrappedClass wc) {
        deleteClassButton.setDisable(false);
        subjectTableView.setItems(accessor().wqueryClassSubjectsOfClass(wc));
    }

    private void loadClasses() {
        classTableView.setItems(accessor().wqueryAllClasses());
    }

    private void numerateLpColumn() {
        // adding numeration of first column
        lpColumn.setCellFactory(Controller.getAutonumerateCallback());
    }

    @FXML
    private void handleAddClassAction(ActionEvent event) {
        Parent root = classTab.getScene().getRoot();
        root.setDisable(true);

        AddClassController
                .interactivelyCreateClass(getApp(), new Stage())
                .ifPresent(c -> classTableView.getItems().add(c));

        root.setDisable(false);
    }

    @FXML
    private void handleAddSubjectAction(ActionEvent event) {
        Parent root = classTab.getScene().getRoot();
        root.setDisable(true);

        AddClassSubjectController
                .interactivelyCreateClassSubject(getApp(), new Stage())
                .ifPresent(c -> subjectTableView.getItems().add(c));

        root.setDisable(false);

    }

    @FXML
    private void handleDeleteClassAction(ActionEvent event) {
        for (WrappedClass wclass : classTableView.getSelectionModel().getSelectedItems()) {
            if (!accessor().wqueryClassStudents(wclass).isEmpty()) {
                showAlertDialog(AlertType.WARNING,
                        "Nie mozna usunac klasy - do klasy sa przypisani uczniowie");
                return;
            }
            accessor().deleteObject(wclass.getObject());
            classTableView.getItems().remove(wclass);
        }
    }
}
