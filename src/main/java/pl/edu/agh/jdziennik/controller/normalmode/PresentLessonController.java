package pl.edu.agh.jdziennik.controller.normalmode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;
import pl.edu.agh.jdziennik.wrapper.WrappedAttendanceValue;
import pl.edu.agh.jdziennik.wrapper.WrappedClass;
import pl.edu.agh.jdziennik.wrapper.WrappedLesson;
import pl.edu.agh.jdziennik.wrapper.WrappedStudentWithPresence;
import pl.edu.agh.jdziennik.wrapper.WrappedTeacher;
import pl.edu.agh.jdziennik.wrapper.WrappedTimeWindow;
import pl.edu.agh.jdziennik.wrapper.WrappedWeekTerm;
import pl.edu.agh.jdziennik.wrapper.WrappedDay;

import java.sql.Date;


public class PresentLessonController extends Controller {

    @FXML
    private ComboBox<WrappedClass> classComboBox;
    @FXML
    private ComboBox<WrappedTimeWindow> timeComboBox;
    @FXML
    private Label subjectLabel;
    @FXML
    private Label classroomLabel;
    @FXML
    private TextArea topicTextArea;
    @FXML
    private Button addLessonButton;

    @FXML
    private TableView<WrappedStudentWithPresence> tableView;
    @FXML
    private TableColumn<WrappedStudentWithPresence, Object> lpColumn;
    @FXML
    private TableColumn<WrappedStudentWithPresence, String> nameColumn;
    @FXML
    private TableColumn<WrappedStudentWithPresence, String> surnameColumn;
    @FXML
    private TableColumn<WrappedStudentWithPresence, String> presenceColumn;
    @FXML
    private Button buttonPresent;
    @FXML
    private Button buttonAbsent;
    @FXML
    private Button buttonLate;
    @FXML
    private Button buttonExcusedAbsence;

    private ObservableList<WrappedClass> teachingClassObservableList = FXCollections.observableArrayList();
    private ObservableList<WrappedStudentWithPresence> studentObservableList = FXCollections.observableArrayList();
    private ObservableList<WrappedTimeWindow> timeWindowObservableList = FXCollections.observableArrayList();

    public PresentLessonController(App app, Stage stage) {
        super(app, stage);

        app.presentLessonTabController = this;
    }

    @FXML
    private void initialize() {
        // only one row can be selected, columns can not be reordered
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        preventColumnReordering(tableView);

        numerateLpColumn();

        fillClassComboBox();
        fillTimeComboBox();
        fillSubjectLabel();
        fillClassroomLabel();
        fillTableView();
        fillTopicTextArea();
        updateAttendance();
        updateButtonsState();
    }

    /**
     * Used by classComboBox
     * Updates shown data in response to class change
     */
    @FXML
    private void handleClassChange(ActionEvent event) {
        updateStudents();
        fillSubjectLabel();
        fillClassroomLabel();
        fillTopicTextArea();
        updateAttendance();
        updateButtonsState();
    }

    /**
     * Used by addLessonButton
     * In case lesson with chosen data and current date exists, function updates it's topic,
     * otherwise creates new lesson object.
     * If some field is empty or wrong data are chosen, alert dialog will be shown.
     */
    @FXML
    private void handleAddLessonAction(ActionEvent event) {
        if (classComboBox.getValue() == null || timeComboBox.getValue() == null || topicTextArea.getText().length() == 0) {
            String alertMessage = "Nie uzupelniono wszystkich pol\n";
            if (classComboBox.getValue() == null) alertMessage += "\nProsze wybrac klase";
            if (timeComboBox.getValue() == null) alertMessage += "\nProsze wybrac godzine";
            if (topicTextArea.getText().length() == 0) alertMessage += "\nProsze wpisac temat";
            showAlertDialog(AlertType.WARNING, alertMessage);
            return;
        }
        if (getCurrentWeekTerm() == null) {
            showAlertDialog(AlertType.ERROR, "Nie znaleziono przedmiotu dla podanych danych\n"
                    + "\nGodzina: " + timeComboBox.getValue().toString()
                    + "\nKlasa: " + classComboBox.getValue().toString());
            return;
        }

        String topic = topicTextArea.getText();
        WrappedWeekTerm weekTerm = getCurrentWeekTerm();

        if (weekTerm == null) {
            showAlertDialog(AlertType.ERROR, "Nie mozna dodac lekcji - prosze sprawdzic wybrane dane");
            return;
        }

        WrappedLesson lesson = accessor().wqueryLesson(new Date(System.currentTimeMillis()), weekTerm);

        if (lesson == null) {
            accessor().createLesson(weekTerm, topic);

            updateButtonsState();
            tableView.getSelectionModel().selectFirst();
        } else {
            accessor().updateLesson(lesson, topic);

            tableView.getSelectionModel().select(null);
        }
    }

    /**
     * Used by timeComboBox
     * Updates shown data in response to time window change
     */
    @FXML
    private void handleTimeWindowChange(ActionEvent event) {
        fillSubjectLabel();
        fillClassroomLabel();
        fillTopicTextArea();
        updateAttendance();
        updateButtonsState();
    }

    /**
     * Used by presence buttons: buttonPresent, buttonAbsent, buttonLate, buttonExcusedAbsence
     * Adds or updates attendance for student selected in tableView
     * Automatically selects student in next row.
     */
    @FXML
    private void handlePresenceCheck(ActionEvent event) {
        WrappedLesson lesson = getLessonToShow();
        WrappedStudentWithPresence student = tableView.getSelectionModel().getSelectedItem();
        WrappedAttendanceValue attendanceValue = WrappedAttendanceValue.getAttendanceValueFromButton((Button) event.getSource());

        if (lesson == null) {
            showAlertDialog(AlertType.ERROR, "Wystapil nieznany blad, prosze sproboac ponownie");
            return;
        }

        if (student == null) {
            showAlertDialog(AlertType.INFORMATION, "Prosze zaznaczyc ucznia");
            return;
        } else student.update(accessor(), lesson);

        if (student.getAttendance() == null)
            accessor().createAttendance(lesson, student, attendanceValue);
        else
            accessor().updateAttendance(student.getAttendance(), attendanceValue);

        student.update(accessor(), lesson);

        // selects next row, clears selection for last row
        if (tableView.getSelectionModel().getSelectedIndex() == studentObservableList.size() - 1)
            tableView.getSelectionModel().clearSelection();
        else
            tableView.getSelectionModel().selectNext();

        tableView.refresh();
    }

    private void fillClassComboBox() {
        teachingClassObservableList.clear();
        teachingClassObservableList.addAll(accessor().wqueryClassesOfTeacher(getApp().getUser()));
        classComboBox.setItems(teachingClassObservableList);

        if (teachingClassObservableList.isEmpty()) classComboBox.setDisable(true);
        else classComboBox.setValue(teachingClassObservableList.get(0));
    }

    private void fillTimeComboBox() {
        timeWindowObservableList.clear();
        timeWindowObservableList.addAll(accessor().wqueryAllTimeWindows());

        timeComboBox.setItems(timeWindowObservableList);
        WrappedTimeWindow currentTimeWindow = accessor().wqueryCurrentTimeWindow();

        if (currentTimeWindow != null)
            timeComboBox.setValue(currentTimeWindow);
    }

    private void fillSubjectLabel() {
        subjectLabel.setText("");

        if (classComboBox.isDisabled()) return;

        WrappedWeekTerm weekTerm = getCurrentWeekTerm();
        if (weekTerm.getObject() != null)
            subjectLabel.setText(weekTerm.getObject().getClassSubject().getSubject().getName());
    }

    private void fillClassroomLabel() {
        classroomLabel.setText("");

        WrappedWeekTerm weekTerm = getCurrentWeekTerm();
        if (weekTerm.getObject() != null)
            classroomLabel.setText(weekTerm.getObject().getClassroom().getId());
    }

    private void fillTopicTextArea() {
        WrappedLesson lessonToShow = getLessonToShow();

        if (lessonToShow != null) topicTextArea.setText(lessonToShow.getObject().getTopic());
        else topicTextArea.setText("");

    }

    private void fillTableView() {
        updateStudents();

        tableView.setItems(studentObservableList);
        nameColumn.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("surname"));
        presenceColumn.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("attendance"));
    }

    private void updateAttendance() {
        WrappedLesson lessonToShow = getLessonToShow();

        for (WrappedStudentWithPresence student : studentObservableList)
            student.update(accessor(), lessonToShow);

        tableView.refresh();
    }

    private void updateStudents() {
        studentObservableList.clear();
        studentObservableList.addAll(accessor().wqueryClassStudentsWithPresence(classComboBox.getValue()));
    }

    /**
     * Enables presence buttons: buttonPresent, buttonAbsent, buttonLate, buttonExcusedAbsence
     * if lesson witch chosen data (class, time window, current date) exists in database
     */
    private void updateButtonsState() {
        boolean disabled;

        if (getLessonToShow() == null) disabled = true;
        else disabled = false;

        buttonPresent.setDisable(disabled);
        buttonAbsent.setDisable(disabled);
        buttonLate.setDisable(disabled);
        buttonExcusedAbsence.setDisable(disabled);

        tableView.getSelectionModel().select(null);
    }

    /**
     * Adds consecutive numbers to first column
     */
    private void numerateLpColumn() {
        lpColumn.setCellFactory(Controller.getAutonumerateCallback());
    }

    /**
     * @return weekTerm witch chosen class, time window and current date if exists in database
     */
    private WrappedWeekTerm getCurrentWeekTerm() {
        if (timeComboBox.getValue() != null && classComboBox.getValue() != null) {
            WrappedTimeWindow timeWindow = timeComboBox.getValue();
            WrappedClass teachingClass = classComboBox.getValue();
            WrappedTeacher teacher = getApp().getUser();
            return accessor().wqueryWeekTerm(WrappedDay.today(), timeWindow, teacher, teachingClass);
        }
        return null;
    }

    /**
     * @return lesson witch chosen class, time window and current date if exists in database
     */
    private WrappedLesson getLessonToShow() {
        WrappedWeekTerm weekTerm = getCurrentWeekTerm();

        if (weekTerm != null)
            return accessor().wqueryLesson(new Date(System.currentTimeMillis()), weekTerm);

        return null;
    }
}
