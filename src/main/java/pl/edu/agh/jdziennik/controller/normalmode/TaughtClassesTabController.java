package pl.edu.agh.jdziennik.controller.normalmode;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

public class TaughtClassesTabController extends Controller {

    @FXML
    private BorderPane taughtPane;

    @FXML
    private TableView<WrappedClassSubject> tableView;

    @FXML
    private TableColumn lpColumn;

    @FXML
    private TableColumn classColumn;

    @FXML
    private TableColumn subjectColumn;

    public TaughtClassesTabController(App app, Stage stage) {
        super(app, stage);

        app.taughtClassesTabController = this;
    }

    private void disableRightSide() {
        getApp().gradePaneController.disablePane();
    }

    @FXML
    private void initialize() {
        disableRightSide();

        classColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedClassSubject, String>
                        ("classId"));

        subjectColumn.setCellValueFactory
                (new PropertyValueFactory<WrappedClassSubject, String>
                        ("subjectName"));

        TableView.TableViewSelectionModel selectionModel =
                tableView.getSelectionModel();

        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        selectionModel.setCellSelectionEnabled(true);

        selectionModel.getSelectedItems().addListener
                (new ListChangeListener<WrappedClassSubject>() {
                    public void onChanged(Change change) {
                        ObservableList<WrappedClassSubject>
                                selection = change.getList();
                        if (selection.size() == 0)
                            disableRightSide();
                        else
                            presentClassSubject(selection.get(0));
                    }
                });

        loadClassSubjects();
    }

    private void presentClassSubject(WrappedClassSubject wcs) {
        getApp().gradePaneController.presentClassSubject(wcs);
    }

    private void loadClassSubjects() {
        tableView.setItems
                (accessor().wqueryClassSubjectsOfTeacher(user()));
    }

}
