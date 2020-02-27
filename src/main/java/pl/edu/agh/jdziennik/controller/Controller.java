package pl.edu.agh.jdziennik.controller;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import pl.edu.agh.jdziennik.App;
import pl.edu.agh.jdziennik.wrapper.WrappedTeacher;
import pl.edu.agh.jdziennik.wrapper.wrappingaccess.WrappingAccessor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Base class for all the controllers.
 * As well as being extended by controllers, this class provides
 * some static methods needed by them.
 */
public abstract class Controller {
    private App app;

    private Stage stage;

    /**
     * Creates a new controller.
     * This constructor must be called in terms of the constructor
     * of the child class.
     * Objects of the child class, however, should never be instantiated
     * directly by other code, but rather with {@link pl.edu.agh.jdziennik.controller.Controller#controllerFromFXML controllerFromFXML() method}.
     *
     * @param app   Is the main application object.
     * @param stage Is an unused javafx stage for the window to be associated with the controller.
     */
    protected Controller(App app, Stage stage) {
        this.app = app;
        this.stage = stage;
    }

    /**
     * Creates a new controller by loading an fxml file.
     * The .fxml under the given path should have it's topmost element
     * bound to a controller class, which extends this class and provides
     * exactly one construcor accepting {@link pl.edu.agh.jdziennik.controller.Controller#Controller the same app and stage arguments}.
     * The new controller object created through fxml shall be returned.
     * Other elements of the .fxml file may be bound to some
     * controller classes, all of which should also fulfill the
     * requirements.
     *
     * @param app      Is the main application object.
     * @param stage    Is an unused javafx stage for the window to be created.
     * @param FXMLPath Represents a path to an .fxml resource describing the window to be created.
     */
    public static <T> T controllerFromFXML(App app, Stage stage,
                                           String FXMLPath) {
        FXMLLoader loader = new FXMLLoader();

        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> c) {
                Object instance = null;

                try {
                    instance = c.getConstructor(App.class,
                            Stage.class)
                            .newInstance(app, stage);
                } catch (NoSuchMethodException |
                        InstantiationException |
                        IllegalAccessException |
                        InvocationTargetException ex) {
                    ex.printStackTrace();

                    System.exit(-1);
                }

                return instance;
            }
        });

        loader.setLocation
                (ClassLoader.getSystemClassLoader()
                        .getResource(FXMLPath));

        try {
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return loader.getController();
    }

    /**
     * Disables drag events on table view.
     * When drag events are disabled, the columns of table view
     * are impossible to reorder by the user.
     * This is an ugly, but java8-compatible way of preventing reorders
     *
     * @param tableView Represents the table, that should have it's columns unreorderable.
     */
    protected static <T> void preventColumnReordering
    (TableView<T> tableView) {
        Platform.runLater(() -> {
            for (Node header :
                    tableView.lookupAll(".column-header")) {
                header.addEventFilter
                        (MouseEvent.MOUSE_DRAGGED, Event::consume);
            }
        });
    }

    /**
     * Provides the Callback, that can be used for table cell in setCellFactory() function
     * to assign consecutive numbers inside rows of this table column,
     * starting with 1, to filled rows in table view
     *
     * @param <T> The type of the objects contained within the TableView items list
     * @param <C> The type of the objects contained within the TableColumn (in this case just Object)
     * @return
     */
    protected static <T, C> Callback<TableColumn<T, C>, TableCell<T, C>> getAutonumerateCallback() {
        return
                new Callback<TableColumn<T, C>, TableCell<T, C>>() {
                    @Override
                    public TableCell call(TableColumn p) {
                        return new TableCell() {
                            @Override
                            public void updateItem(Object item, boolean empty) {
                                super.updateItem(item, empty);
                                setGraphic(null);
                                setText(empty ? null : getIndex() + 1 + "");
                            }
                        };
                    }
                };
    }

    /**
     * Shows to the user an alert dialog with message in it.
     * Returns after the dialog is closed.
     *
     * @param type    Represents type of the dialog to show.
     * @param message Is the text to be displayed in the dialog.
     */
    protected static void showAlertDialog(AlertType type, String message) {
        Alert alert = new Alert(type.alertType);
        alert.setTitle("UWAGA");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Provides the WrappingAccessor used by this application instance.
     * This method is a convenience proxy to getAccessor() method of
     * contained App object.
     */
    protected WrappingAccessor accessor() {
        return app.getAccessor();
    }

    /**
     * Provides the current user of this application instance.
     * Should only be used in normal mode, never in admin mode.
     * This method is a convenience proxy to getUser() method of
     * contained App object.
     */
    protected WrappedTeacher user() {
        return app.getUser();
    }

    /**
     * Returns the javafx stage associated with the controller/window.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Returns the App object representing the application instance in
     * terms of which this controller works.
     */
    public App getApp() {
        return app;
    }

    /**
     * Represents possible types of alerts, that can be shown.
     */
    protected static enum AlertType {
        INFORMATION(Alert.AlertType.INFORMATION),
        WARNING(Alert.AlertType.WARNING),
        ERROR(Alert.AlertType.ERROR);

        private final Alert.AlertType alertType;

        AlertType(Alert.AlertType alertType) {
            this.alertType = alertType;
        }
    }
}
