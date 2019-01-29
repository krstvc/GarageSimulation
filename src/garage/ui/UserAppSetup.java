package garage.ui;

import garage.Garage;
import garage.simulation.control.RandomVehicleGenerator;
import garage.simulation.control.PathCalculator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Random;

public class UserAppSetup {

    private static boolean initialized = false;

    public static Stage stage;
    public static Scene scene;
    public static VBox layout;

    public static Label numberOfVehiclesLabel = new Label("Number of vehicles");
    public static TextField numberOfVehiclesInput = new TextField();

    public static Button continueButton = new Button("_Continue");

    public static void initialize() {
        if (!initialized) {
            setupTextField();
            setupContinueButton();

            layout = new VBox(8);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(12));
            layout.getChildren().addAll(numberOfVehiclesLabel, numberOfVehiclesInput, continueButton);

            stage = new Stage();
            stage.setTitle("Garage | Setting up user app");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            scene = new Scene(layout, screenSize.width / 4, screenSize.height / 4);
            stage.setScene(scene);

            initialized = true;
        }
        numberOfVehiclesInput.clear();
        stage.show();
    }

    private static void setupTextField() {
        numberOfVehiclesInput.setPromptText("Minimum number of vehicles to start simulation with");
        numberOfVehiclesInput.setAlignment(Pos.CENTER);

        numberOfVehiclesInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*"))
                    numberOfVehiclesInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private static void setupContinueButton() {
        continueButton.disableProperty().bind(numberOfVehiclesInput.textProperty().isEqualTo(""));

        continueButton.setOnAction(event -> {
            if (validNumberOfVehicles()) {
                new Thread(() -> generateAndAddRandomVehiclesIfNeeded()).start();
                new Thread(() -> PathCalculator.calculatePaths()).start();

                UserAppHome.initialize();
                stage.hide();
                AdminAppHome.stage.hide();
            } else {
                PopUp.displayWarningInfo(
                        "Warning",
                        "Only " + Garage.getPlatforms().size() * 28 + " spots available",
                        "Please enter a lower number."
                );
                numberOfVehiclesInput.clear();
            }
        });
    }

    private static void generateAndAddRandomVehiclesIfNeeded() {
        Random random = new Random();
        int minimumNumberOfVehicles = Integer.parseInt(numberOfVehiclesInput.getText());
        if (minimumNumberOfVehicles > Garage.getVehicleHashMap().size()) {
            int toAdd = minimumNumberOfVehicles - Garage.getVehicleHashMap().size();

            for (int i = 0; i < toAdd; ++i) {
                int platformIndex;
                do {
                    platformIndex = random.nextInt(Garage.getPlatforms().size());
                } while (Garage.getPlatforms().get(platformIndex).isFull());
                Garage.getPlatforms().get(platformIndex).findRandomPositionAndPark(RandomVehicleGenerator.generateNewRandomVehicle());
            }
        }
    }

    private static boolean validNumberOfVehicles() {
        return Integer.parseInt(numberOfVehiclesInput.getText()) <= Garage.getPlatforms().size() * 28;
    }

}
