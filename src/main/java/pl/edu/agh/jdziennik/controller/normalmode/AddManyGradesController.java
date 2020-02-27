package pl.edu.agh.jdziennik.controller.normalmode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;
import pl.edu.agh.jdziennik.wrapper.WrappedClass;
import pl.edu.agh.jdziennik.wrapper.WrappedClassSubject;
import pl.edu.agh.jdziennik.wrapper.WrappedGradeType;
import pl.edu.agh.jdziennik.wrapper.WrappedGradeValue;
import pl.edu.agh.jdziennik.wrapper.WrappedStudentWithGrade;
import pl.edu.agh.jdziennik.wrapper.WrappedStudentWithPresence;
import pl.edu.agh.jdziennik.wrapper.WrappedStudentWithSubject;
import pl.edu.agh.jdziennik.wrapper.WrappedSubject;

import java.util.LinkedList;
import java.util.List;


public class AddManyGradesController extends Controller {

    @FXML
    private Label classLabel;
    @FXML
    private Label subjectLabel;
    @FXML
    private ComboBox<WrappedGradeType> gradeTypeComboBox;
    @FXML
    private TextArea commentTextArea;
    @FXML
    private TableView<WrappedStudentWithGrade> tableView;
    @FXML
    private TableColumn lpColumn;
    @FXML
    private TableColumn surnameColumn;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn gradeColumn;

    private ObservableList<WrappedStudentWithGrade> studentObservableList = FXCollections.observableArrayList();
    private ObservableList<WrappedGradeType> gradeTypes = FXCollections.observableArrayList(accessor().wqueryGradeTypes(getApp().getUser()));

    public AddManyGradesController(App app, Stage stage) {
        super(app, stage);
    }

    /**
     * Static method, that can be called to show AddManyGradesWindow
     *
     * @param wSubject subject, for which grade will be added
     * @param wClass   class, for which user wants to add grades
     * @return subject
     */
    public static void newAddGradesWindow(App app, Stage stage, WrappedClass wClass, WrappedSubject wSubject) {

        AddManyGradesController controller = controllerFromFXML(app, stage, "view/normalmode/AddManyGradesWindow.fxml");

        if (wClass == null || wClass.getObject() == null || wSubject == null || wSubject.getObject() == null)
            Controller.showAlertDialog(AlertType.ERROR, "Proszę wybrać poprawne wartości danych");

        controller.fillWithData(wClass, wSubject);

        stage.showAndWait();

        // Saves chosen grades in the database
        for (WrappedStudentWithGrade student : controller.studentObservableList) {
            if (student.getGradeValue() == null)
                continue;

            String comment = controller.commentTextArea.getText();

            WrappedClassSubject currentClassSubject = controller.accessor().wqueryClassSubject(student, controller.getApp().getUser());

            controller.accessor().createGrade(student.getObject(), currentClassSubject.getObject(), student.getGradeValue().getObject(), controller.gradeTypeComboBox.getValue().getObject(), comment);
        }

    }

    @FXML
    private void initialize() {
        getStage().setTitle("Jdziennik - dodawanie ocen");
        preventColumnReordering(tableView);
        tableView.setSelectionModel(null);

        gradeTypeComboBox.setItems(gradeTypes);

        numerateLpColumn();
    }

    /**
     * Method that fills data in AddManyGradesWindow
     * Must be called by static method that shows the window
     *
     * @param wrappedClass   class, for which user wants to add grades
     * @param wrappedSubject subject, for which grade will be added
     */
    private void fillWithData(WrappedClass wrappedClass, WrappedSubject wrappedSubject) {
        fillStudents(wrappedClass, wrappedSubject);
        fillColumnWithButtons();
        classLabel.setText(wrappedClass.getObject().getId());
        subjectLabel.setText(wrappedSubject.getObject().getName());
    }

    /**
     * Fills tableView with students
     */
    private void fillStudents(WrappedClass wrappedClass, WrappedSubject wrappedSubject) {
        studentObservableList.addAll(accessor().wqueryClassStudentsWithGrade(wrappedClass.getObject()));

        for (WrappedStudentWithGrade student : studentObservableList) {
            student.setTeachingClass(wrappedClass);
            student.setSubject(wrappedSubject);
        }

        tableView.setItems(studentObservableList);

        nameColumn.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<WrappedStudentWithPresence, String>("surname"));
    }

    /**
     * Adds consecutive numbers to first column of tableView
     */
    private void numerateLpColumn() {
        // adding numeration of first column
        lpColumn.setCellFactory(Controller.getAutonumerateCallback());
    }


    /**
     * Used by gradeTypeComboBox
     * Enables tableView when gradeType and subject are chosen
     */
    @FXML
    public void handleGradeComboBoxChange(ActionEvent event) {
        if (gradeTypeComboBox.getValue() != null)
            tableView.setDisable(false);
    }

    /**
     * Adds HBox with buttons to gradeColumn in tableView
     * Using these buttons user can choose gradeValue for student in row
     */
    private void fillColumnWithButtons() {
        Callback<TableColumn<WrappedStudentWithSubject, Void>, TableCell<WrappedStudentWithSubject, Void>> cellFactory =
                new Callback<TableColumn<WrappedStudentWithSubject, Void>, TableCell<WrappedStudentWithSubject, Void>>() {
                    @Override
                    public TableCell<WrappedStudentWithSubject, Void> call(final TableColumn<WrappedStudentWithSubject, Void> param) {
                        final TableCell<WrappedStudentWithSubject, Void> cell = new TableCell<WrappedStudentWithSubject, Void>() {

                            HBox buttons = getButtons();

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(buttons);
                                }
                            }

                            private HBox getButtons() {
                                HBox buttonsBox = new HBox();
                                buttonsBox.setSpacing(5.0);

                                List<Button> gradeButtons = new LinkedList<>();

                                for (WrappedGradeValue value : WrappedGradeValue.values()) {
                                    Button button = new Button(value.toString());
                                    button.setMinWidth(35.0);
                                    button.setMaxWidth(35.0);
                                    button.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
                                    button.setOnMouseClicked(event -> {
                                        for (Button b : gradeButtons)
                                            b.setBackground(new Background(new BackgroundFill(null, null, null)));
                                        button.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));

                                        tableView.getItems().get(getIndex()).setGradeValue(WrappedGradeValue.fromString(button.getText()));
                                    });
                                    gradeButtons.add(button);
                                }

                                buttonsBox.getChildren().addAll(gradeButtons);

                                return buttonsBox;
                            }

                        };

                        return cell;
                    }
                };


        gradeColumn.setCellFactory(cellFactory);
    }
}
