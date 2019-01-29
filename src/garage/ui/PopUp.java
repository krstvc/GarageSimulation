package garage.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.util.Duration;

public class PopUp {

    private static long duration = 3;

    public static void displayErrorInfo(String title, String headerText, String contentText) {
        display(title, headerText, contentText, Alert.AlertType.ERROR);
    }

    public static void displayWarningInfo(String title, String headerText, String contentText) {
        display(title, headerText, contentText, Alert.AlertType.WARNING);
    }

    private static void display(String title, String headerText, String contentText, Alert.AlertType alertType) {
        Thread displayed = new Thread(() ->
                Platform.runLater(() -> {
                    Alert alert = new Alert(alertType);
                    alert.setTitle(title);
                    alert.setHeaderText(headerText);
                    alert.setContentText(contentText);
                    alert.show();

                    PauseTransition pause = new PauseTransition(Duration.seconds(duration));
                    pause.setOnFinished(event -> alert.hide());
                    pause.play();
                }));

        displayed.setDaemon(true);
        displayed.start();
    }

}
