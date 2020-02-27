package pl.edu.agh.jdziennik.controller.normalmode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;
import pl.edu.agh.jdziennik.wrapper.WrappedClass;
import pl.edu.agh.jdziennik.wrapper.WrappedGrade;
import pl.edu.agh.jdziennik.wrapper.WrappedStudentWithPresence;
import pl.edu.agh.jdziennik.wrapper.WrappedStudentWithSubject;
import pl.edu.agh.jdziennik.wrapper.WrappedSubject;

import java.util.Optional;

public class GradeTabController extends Controller {

    @FXML
    private BorderPane rootPane;
    @FXML
    private ComboBox<WrappedClass> classComboBox;
    @FXML
    private ComboBox<WrappedSubject> subjectComboBox;
    @FXML
    private Button addManyGradesButton;
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
    @FXML
    private TableColumn newGradeColumn;
    @FXML
    private TableColumn avgGradeColumn;

    private ObservableList<WrappedClass> teachingClassObservableList = FXCollections.observableArrayList();
    private ObservableList<WrappedSubject> subjectObservableList = FXCollections.observableArrayList();
    private ObservableList<WrappedStudentWithSubject> studentObservableList = FXCollections.observableArrayList();

    public GradeTabController(App app, Stage stage) {
        super(app, stage);

        app.gradeTabController = this;
    }

    @FXML
    private void initialize() {
        // only one row can be selected
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        preventColumnReordering(tableView);

        numerateLpColumn();

        fillClassComboBox();
        fillSubjectComboBox();
        fillTableView();
        fillColumnWithButtons();
    }

    /**
     * Used by classComboBox
     * Reloads subjects and students when class changes
     */
    @FXML
    private void handleClassChange(ActionEvent event) {
        subjectComboBox.getSelectionModel().clearSelection();
        studentObservableList.clear();
        studentObservableList.addAll(accessor().wqueryClassStudentsWithSubject(classComboBox.getValue().getObject()));
    }

    /**
     * Used by subjectComboBox
     * Updates each student, so that displayed grades fit chosen subject
     */
    @FXML
    private void handleSubjectChange(ActionEvent event) {
        WrappedSubject subject = subjectComboBox.getValue();

        if (subject != null) {
            studentObservableList.forEach(s -> {
                s.setSubject(subject.getObject());
                s.update(accessor());
            });

            addManyGradesButton.setDisable(false);
        } else
            addManyGradesButton.setDisable(true);
    }

    /**
     * Used by addMnyGradesButton
     * Opens AddManyGradesWindow, that allows user to add grades for all students at once
     */
    @FXML
    private void handleAddManyGradesAction(ActionEvent event) {
        rootPane.setDisable(true);

        AddManyGradesController.newAddGradesWindow(getApp(), new Stage(), classComboBox.getValue(), subjectComboBox.getValue());

        for (WrappedStudentWithSubject student : studentObservableList)
            student.update(accessor());

        rootPane.setDisable(false);
    }

    private void fillClassComboBox() {
        teachingClassObservableList.clear();
        teachingClassObservableList.addAll(accessor().wqueryClassesOfTeacher(getApp().getUser()));
        classComboBox.setItems(teachingClassObservableList);

        if (teachingClassObservableList.isEmpty()) classComboBox.setDisable(true);
        else classComboBox.setValue(teachingClassObservableList.get(0));
    }

    private void fillSubjectComboBox() {
        if (classComboBox.isDisabled()) return;

        subjectObservableList.addAll(accessor().wquerySubjectsOfTeacherAndClass(getApp().getUser(), classComboBox.getValue()));
        subjectComboBox.setItems(subjectObservableList);
    }

    private void fillTableView() {
        studentObservableList.addAll(accessor().wqueryClassStudentsWithSubject(classComboBox.getValue().getObject()));

        tableView.setItems(studentObservableList);

        nameColumn.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("surname"));

        gradeColumn.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("grades"));
        avgGradeColumn.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("avgGrade"));
    }

    /**
     * Fills newGradeColumn with buttons, one button for each row
     * The button, when clicked, opens AddGradeWindow, that allows to add new grade for student in row
     */
    private void fillColumnWithButtons() {
        Callback<TableColumn<WrappedStudentWithSubject, Void>, TableCell<WrappedStudentWithSubject, Void>> cellFactory =
                new Callback<TableColumn<WrappedStudentWithSubject, Void>, TableCell<WrappedStudentWithSubject, Void>>() {
                    @Override
                    public TableCell<WrappedStudentWithSubject, Void> call(final TableColumn<WrappedStudentWithSubject, Void> param) {
                        final TableCell<WrappedStudentWithSubject, Void> cell = new TableCell<WrappedStudentWithSubject, Void>() {

                            private final Button btn = new Button("+");

                            {
                                btn.setOnAction((ActionEvent event) -> {
                                    rootPane.setDisable(true);

                                    Optional<WrappedGrade> maybeGrade = AddGradeController.newAddGradeWindow(getApp(), new Stage(), tableView.getItems().get(getIndex()));

                                    // maybe replace that if block with passing
                                    // a labmda to Optional to perform if value is present?
                                    if (maybeGrade.isPresent()) {
                                        tableView.getItems().get(getIndex()).update(accessor());

                                        tableView.refresh();
                                    }

                                    rootPane.setDisable(false);
                                });

                                btn.setMinWidth(98);
                                btn.setMaxWidth(98);
                                btn.setStyle("-fx-background-color: transparent;");
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(btn);
                                }
                            }
                        };

                        return cell;
                    }
                };


        newGradeColumn.setCellFactory(cellFactory);
    }

    /**
     * Adds consecutive numbers to first column of tableView
     */
    private void numerateLpColumn() {
        // adding numeration of first column
        lpColumn.setCellFactory(Controller.getAutonumerateCallback());
    }
}
