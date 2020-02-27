package pl.edu.agh.jdziennik.controller.normalmode;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.controller.Controller;

public class AppWindowController extends Controller {
    public AppWindowController(App app, Stage stage) {
        super(app, stage);

        app.appWindowController = this;
    }

    @FXML
    private void initialize() {
//	    getStage().setMaximized(true);
        getStage().setTitle("Jdziennik");
    }
}
