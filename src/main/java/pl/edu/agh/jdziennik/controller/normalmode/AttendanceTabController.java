package pl.edu.agh.jdziennik.controller.normalmode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;
import pl.edu.agh.jdziennik.wrapper.WrappedAttendance;
import pl.edu.agh.jdziennik.wrapper.WrappedAttendanceValue;
import pl.edu.agh.jdziennik.wrapper.WrappedClass;
import pl.edu.agh.jdziennik.wrapper.WrappedStudentWithPresence;

import java.sql.Date;
import java.time.LocalDate;


public class AttendanceTabController extends Controller {

    @FXML
    private ComboBox<WrappedClass> classComboBox;
    @FXML
    private TableView<WrappedStudentWithPresence> tableView;
    @FXML
    private TableColumn<WrappedStudentWithPresence, Object> lpColumn;
    @FXML
    private TableColumn<WrappedStudentWithPresence, String> nameColumn;
    @FXML
    private TableColumn<WrappedStudentWithPresence, String> surnameColumn;
    @FXML
    private TableColumn<WrappedStudentWithPresence, String> presenceCount;
    @FXML
    private TableColumn<WrappedStudentWithPresence, String> excusedAbsenceCount;
    @FXML
    private TableColumn<WrappedStudentWithPresence, String> lateCount;
    @FXML
    private TableColumn<WrappedStudentWithPresence, String> absenceCount;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private ListView<WrappedAttendance> attendanceListView;
    @FXML
    private Button attendanceChangeButton;

    private ObservableList<WrappedClass> teachingClassObservableList = FXCollections.observableArrayList();
    private ObservableList<WrappedStudentWithPresence> studentObservableList = FXCollections.observableArrayList();
    private ObservableList<WrappedAttendance> attendanceObservableList = FXCollections.observableArrayList();

    public AttendanceTabController(App app, Stage stage) {
        super(app, stage);

        app.attendanceTabController = this;
    }

    @FXML
    private void initialize() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        fillClassComboBox();
        fillDatePickers();
        fillTableView();
        fillListView();
    }

    /**
     * Used by classComboBox
     * Reloads student list and updates attendance fields for each student
     */
    @FXML
    private void handleClassChange(ActionEvent event) {
        fillDatePickers();

        studentObservableList.clear();
        studentObservableList.addAll(accessor().wqueryClassStudentsWithPresence(classComboBox.getValue()));

        updateAttendances();

        tableView.getSelectionModel().select(null);
    }

    /**
     * Used by attendanceChangeButton
     * Allows to change attendance value from NIEOBECNOSC to NIEOBECNOSC_USPRAWIEDLIWIONA
     * for attendance chosen from attendanceListView
     */
    @FXML
    private void handlePresenceChange(ActionEvent event) {
        WrappedStudentWithPresence student = tableView.getSelectionModel().getSelectedItem();
        WrappedAttendance wAttendance = attendanceListView.getSelectionModel().getSelectedItem();

        if (wAttendance == null) {
            showAlertDialog(AlertType.ERROR, "Wystapil nieznany blad, prosze sproboac ponownie");
            return;
        }

        accessor().updateAttendance(wAttendance, WrappedAttendanceValue.getAttendanceValueFromString("NIEOBECNOSC_USPRAWIEDLIWIONA"));

        student.setAttendance(accessor(), Date.valueOf(fromDatePicker.getValue()), Date.valueOf(toDatePicker.getValue()));

        attendanceObservableList.remove(wAttendance);
    }

    /**
     * Used by data pickers
     * Updates attendance fields to fit between chosen dates
     */
    @FXML
    private void handleDateChange(ActionEvent event) {
        updateAttendances();
    }

    private void fillClassComboBox() {
        teachingClassObservableList.clear();
        teachingClassObservableList.addAll(accessor().wqueryClassesOfTeacher(getApp().getUser()));

        classComboBox.setItems(teachingClassObservableList);

        if (teachingClassObservableList.isEmpty()) classComboBox.setDisable(true);
        else classComboBox.setValue(teachingClassObservableList.get(0));
    }

    /**
     * Sets default dates for date pickers as - from: two weeks ago, to: today
     */
    private void fillDatePickers() {
        fromDatePicker.setValue(LocalDate.now().minusDays(14));
        toDatePicker.setValue(LocalDate.now());
    }

    private void fillTableView() {
        studentObservableList.addAll(accessor().wqueryClassStudentsWithPresence(classComboBox.getValue()));

        tableView.setItems(studentObservableList);
        nameColumn.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("surname"));
        presenceCount.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("presenceCount"));
        excusedAbsenceCount.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("excusedAbsenceCount"));
        lateCount.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("lateCount"));
        absenceCount.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("absenceCount"));

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
                updateAttendances();
        });

        numerateLpColumn();
        updateAttendances();
    }

    private void fillListView() {
        attendanceListView.setItems(attendanceObservableList);

        attendanceListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) attendanceChangeButton.setDisable(false);
            else attendanceChangeButton.setDisable(true);
        });
    }

    private void updateAttendances() {
        for (WrappedStudentWithPresence student : studentObservableList)
            student.setAttendance(accessor(), Date.valueOf(fromDatePicker.getValue()), Date.valueOf(toDatePicker.getValue()));

        attendanceObservableList.clear();

        if (tableView.getSelectionModel().getSelectedItem() != null)
            attendanceObservableList.addAll(tableView.getSelectionModel().getSelectedItem().getAbsence());
    }

    /**
     * Adds consecutive numbers to first column
     */
    private void numerateLpColumn() {
        lpColumn.setCellFactory(Controller.getAutonumerateCallback());
    }

}
