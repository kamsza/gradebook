package pl.edu.agh.jdziennik.controller.normalmode;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;
import pl.edu.agh.jdziennik.wrapper.WrappedClassSubject;
import pl.edu.agh.jdziennik.wrapper.WrappedStudent;
import pl.edu.agh.jdziennik.wrapper.WrappedStudentWithSubject;

public class GradePaneController extends Controller {

    @FXML
    private BorderPane gradePane;

    @FXML
    private TableView<WrappedStudentWithSubject> tableView;

    @FXML
    private TableColumn lpColumn;

    @FXML
    private TableColumn nameColumn;

    @FXML
    private TableColumn surnameColumn;

    @FXML
    private TableColumn gradeColumn;

    public GradePaneController(App app, Stage stage) {
        super(app, stage);

        app.gradePaneController = this;
    }

    public void disablePane() {
        tableView.setItems(FXCollections.observableArrayList());
        gradePane.setDisable(true);
    }

    @FXML
    private void initialize() {
        // only one row can be selected
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        numerateLpColumn();

        // adding student names and surnames to columns
        nameColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedStudent, String>
                        ("name"));
        surnameColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedStudent, String>
                        ("surname"));
        gradeColumn.setCellValueFactory
                (new PropertyValueFactory
                        <WrappedStudentWithSubject, String>("grade"));
    }

    public void presentClassSubject(WrappedClassSubject classSubject) {
        // adding items to table as ObservableList of
        // WrappedStudentWithSubject from database

        tableView.setItems(accessor().wqueryClassStudentsWithClassSubject(classSubject));

        gradePane.setDisable(false);
    }

    private void numerateLpColumn() {
        // adding numeration of first column
        lpColumn.setCellFactory(Controller.getAutonumerateCallback());
    }

    @FXML
    private void handleAddGradeAction(ActionEvent event) {
        //        TablePosition selectedCell = (TablePosition)tableView.getSelectionModel().getSelectedCells().get(0);
        //        Student selectedStudent = studentObservableList.get(selectedCell.getRow());
    }


}
