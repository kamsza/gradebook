package pl.edu.agh.jdziennik;

import javafx.application.Application;
import javafx.stage.Stage;
import org.hibernate.cfg.Configuration;
import pl.edu.agh.jdziennik.controller.Controller;
import pl.edu.agh.jdziennik.controller.UserSelectionController;
import pl.edu.agh.jdziennik.controller.adminmode.AdminAppWindowController;
import pl.edu.agh.jdziennik.controller.adminmode.ClassTabController;
import pl.edu.agh.jdziennik.controller.adminmode.StudentTabController;
import pl.edu.agh.jdziennik.controller.adminmode.TeacherTabController;
import pl.edu.agh.jdziennik.controller.normalmode.AppWindowController;
import pl.edu.agh.jdziennik.controller.normalmode.AttendanceTabController;
import pl.edu.agh.jdziennik.controller.normalmode.GradePaneController;
import pl.edu.agh.jdziennik.controller.normalmode.GradeTabController;
import pl.edu.agh.jdziennik.controller.normalmode.PresentLessonController;
import pl.edu.agh.jdziennik.controller.normalmode.TaughtClassesTabController;
import pl.edu.agh.jdziennik.wrapper.WrappedTeacher;
import pl.edu.agh.jdziennik.wrapper.wrappingaccess.WrappingAccessor;

import java.util.Optional;

/**
 * Represents the application instance.
 * This is the main class of the application (i.e. it contains main()).
 * Object of this class holds references to other vital elements
 * of the application.
 */
public class App extends Application {
    // controllers used in normal mode

    /**
     * Reference to currently-used controller of application window.
     * Used only in normal mode - shall be null if in admin mode.
     * Set from AppWindowController's constructor.
     */
    public AppWindowController appWindowController;

    /**
     * Reference to currently-used controller of present lesson tab.
     * Used only in normal mode - shall be null if in admin mode.
     * Set from AttendanceTabController's constructor.
     */
    public PresentLessonController presentLessonTabController;

    /**
     * Reference to currently-used controller of attendances tab.
     * Used only in normal mode - shall be null if in admin mode.
     * Set from AttendanceTabController's constructor.
     */
    public AttendanceTabController attendanceTabController;

    /**
     * Reference to currently-used controller of grades pane.
     * Used only in normal mode - shall be null if in admin mode.
     * Set from GradePaneController's constructor.
     */
    public GradePaneController gradePaneController;


    /**
     * Reference to currently-used controller of grades tab.
     * Used only in normal mode - shall be null if in admin mode.
     * Set from GradeTabController's constructor.
     */
    public GradeTabController gradeTabController;

    /**
     * Reference to currently-used controller of taught classes tab.
     * Used only in normal mode - shall be null if in admin mode.
     * Set from TaughtClassesTabController's constructor.
     */
    public TaughtClassesTabController taughtClassesTabController;

    // controllers used in admin mode

    /**
     * Reference to currently-used controller of application window in admin mode.
     * Used only in admin mode - shall be null if in normal mode.
     * Set from AdminAppWindowController's constructor.
     */
    public AdminAppWindowController adminAppWindowController;

    /**
     * Reference to currently-used controller of students tab.
     * Used only in admin mode - shall be null if in normal mode.
     * Set from StudentTabController's constructor.
     */
    public StudentTabController studentTabController;

    /**
     * Reference to currently-used controller of classes tab.
     * Used only in admin mode - shall be null if in normal mode.
     * Set from ClassTabController's constructor.
     */
    public ClassTabController classTabController;

    /**
     * Reference to currently-used controller of teachers tab.
     * Used only in admin mode - shall be null if in normal mode.
     * Set from TeachersTabController's constructor.
     */
    public TeacherTabController teacherTabController;

    /**
     * Reference to WrappingAccessor, which provides access to the database.
     * Set from start().
     */
    private WrappingAccessor accessor;

    /**
     * Reference to WrappedTeacher representing the user of the application.
     * Must contain a WrappedTeacher when in normal mode.
     * Shall be empty if in admin mode.
     * Set from start().
     */
    private Optional<WrappedTeacher> currentUser;

    /**
     * Creates new WrappingAccessor using a new hibernate session.
     * Uses the default hibernate configuration.
     */
    private static WrappingAccessor initializeAccessor() {
        return new WrappingAccessor
                (new Configuration()
                        .configure("hibernate/hibernate.cfg.xml")
                        .buildSessionFactory()
                        .openSession());
    }

    /**
     * Application entry point.
     * If the database is empty, initializes it with example data.
     * Calls javafx.
     */
    public static void main(String[] args) {
        WrappingAccessor temporaryAccessor = initializeAccessor();

        new DataGenerator(temporaryAccessor).generateData();

        // we don't reuse it, because there's no simple way to pass
        // it to the application (we don't want static fields)
        temporaryAccessor.close();

        launch(args);

        System.exit(0);
    }

    /**
     * Returns the WrappingAccessor used to access the database.
     */
    public WrappingAccessor getAccessor() {
        return accessor;
    }

    /**
     * Returns WrappedTeacher representing the current user.
     * Should only be used in normal mode.
     * Undefined behaviour when called in admin mode.
     */
    public WrappedTeacher getUser() {
        return currentUser.get();
    }

    /**
     * for javafx
     * Entry point of the GUI logic.
     * Initializes the App object, shows user selection dialog and
     * afterwards spawns application window in either admin or normal
     * mode (depending on user choice).
     */
    @Override
    public void start(Stage primaryStage) {
        accessor = initializeAccessor();

        Stage userSelectionStage = new Stage();

        userSelectionStage.setOnCloseRequest(event -> {
            accessor.close();
            System.exit(0);
        });

        primaryStage.setOnCloseRequest(userSelectionStage.getOnCloseRequest());

        currentUser = UserSelectionController
                .interactivelySelectUser(this, userSelectionStage);

        String resourceName =
                "view/" + (currentUser.isPresent() ?
                        "normalmode/AppWindow" :
                        "adminmode/AdminAppWindow") +
                        ".fxml";


        Controller.<Controller>controllerFromFXML
                (this, primaryStage, resourceName).getStage().show();
    }

}
