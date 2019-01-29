package garage.simulation;

import garage.Garage;
import garage.ui.AdminAppHome;
import garage.ui.PopUp;
import javafx.application.Application;
import javafx.stage.Stage;



public class Start extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Garage.setupGarage();
            AdminAppHome.initialize();
        } catch (Exception exception) {
            PopUp.displayErrorInfo(
                    "Error",
                    "Unable to set up the garage",
                    "Application is about to close." + System.lineSeparator() + System.lineSeparator()
                            + "Possible reasons:" + System.lineSeparator()
                            + "     o Some of the required files might be corrupt" + System.lineSeparator()
                            + "     o I/O error occurred"
            );
        }
    }

}
