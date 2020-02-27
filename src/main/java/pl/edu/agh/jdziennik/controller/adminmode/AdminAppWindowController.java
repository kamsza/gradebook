package pl.edu.agh.jdziennik.controller.adminmode;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;

public class AdminAppWindowController extends Controller {
    public AdminAppWindowController(App app, Stage stage) {
        super(app, stage);

        app.adminAppWindowController = this;
    }

    @FXML
    private void initialize() {
        getStage().setTitle("Jdziennik - tryb administratora");
    }
}
