package garage.ui;

import garage.Garage;
import garage.Platform;
import garage.simulation.control.*;
import garage.utility.logging.GarageLogger;
import garage.vehicles.Vehicle;
import garage.vehicles.VehicleType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class UserAppHome {

    public static Stage stage;
    public static Scene scene;
    public static BorderPane layout = new BorderPane();

    public static VBox topVBox = new VBox(4);
    public static HBox topHBox = new HBox(12);
    public static HBox addAndRemoveButtonsBox = new HBox(8);
    public static HBox switchBox = new HBox();
    public static HBox bottomBox = new HBox();

    public static MenuBar menuBar = new MenuBar();

    public static Menu addVehicleMenu = new Menu("_Add new vehicle");
    public static Menu civilVehicleMenu = new Menu("_Civil vehicle");
    public static Menu policeVehicleMenu = new Menu("_Police vehicle");
    public static Menu sanitaryVehicleMenu = new Menu("_Sanitary vehicle");
    public static Menu firefightingVehicleMenu = new Menu("_Firefighting vehicle");

    public static MenuItem civilCarItem = new MenuItem("Civil car");
    public static MenuItem civilMotorcycleItem = new MenuItem("Civil motorcycle");
    public static MenuItem civilVanItem = new MenuItem("Civil van");
    public static MenuItem policeCarItem = new MenuItem("Police car");
    public static MenuItem policeMotorcycleItem = new MenuItem("Police motorcycle");
    public static MenuItem policeVanItem = new MenuItem("Police van");
    public static MenuItem sanitaryCarItem = new MenuItem("Sanitary car");
    public static MenuItem sanitaryVanItem = new MenuItem("Sanitary van");
    public static MenuItem firefightingVanItem = new MenuItem("Firefighting van");

    public static Label simulationSpeedLabel = new Label("Simulation speed:");
    public static Slider simulationSpeedSlider = new Slider();

    public static Button addNewRandomVehicleButton = new Button("_Add new random vehicle");
    public static Button forceVehiclesToExitButton = new Button("_Force vehicles to exit");
    public static Button switchToAdminAppButton = new Button("_Switch to admin app");

    private static final int GRID_WIDTH = 8;
    private static final int GRID_LENGTH = 10;
    public static GridPane gridPane = new GridPane();
    public static StackPane[][] fieldMatrix = new StackPane[GRID_WIDTH][GRID_LENGTH];

    public static ComboBox<Platform> platformComboBox = new ComboBox<>();


    public static void initialize() {
        if (stage == null) {
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    try {
                        Garage.writeToSerializationFile();
                    } catch (IOException exception) {
                        GarageLogger.log(Level.WARNING, "I/O error occurred", exception);
                    }
                    javafx.application.Platform.exit();
                }
            });

            setupMenus();
            setupTop();
            setupBottom();
            setupGrid();

            layout = new BorderPane(gridPane, topVBox, null, bottomBox, null);
            layout.setBackground(new Background(
                    new BackgroundFill(Color.LAVENDER, CornerRadii.EMPTY, new Insets(5))
            ));

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            stage.setMinWidth(screenSize.width / 2.5);
            stage.setMinHeight(screenSize.height / 1.3);
            scene = new Scene(layout, screenSize.width / 2.2, screenSize.height / 1.2);

            stage.setTitle("Garage | User app");
            stage.setScene(scene);
        }

        stage.show();
    }

    private static void setupTop() {
        setupButtons();
        addAndRemoveButtonsBox.setAlignment(Pos.CENTER_LEFT);
        addAndRemoveButtonsBox.setPadding(new Insets(0, 8, 8, 8));
        addAndRemoveButtonsBox.getChildren().addAll(menuBar, addNewRandomVehicleButton, forceVehiclesToExitButton);

        switchBox.setAlignment(Pos.CENTER_RIGHT);
        switchBox.setPadding(new Insets(0, 8, 8, 8));
        switchBox.getChildren().add(switchToAdminAppButton);

        topHBox.getChildren().addAll(addAndRemoveButtonsBox, switchBox);

        HBox.setHgrow(addAndRemoveButtonsBox, Priority.ALWAYS);
        HBox.setHgrow(switchBox, Priority.NEVER);

        setupSlider();

        topVBox.setAlignment(Pos.CENTER);
        topVBox.setPadding(new Insets(8));
        topVBox.setBackground(new Background(
                new BackgroundFill(Color.LIGHTSTEELBLUE, CornerRadii.EMPTY, new Insets(2))
        ));
        topVBox.getChildren().addAll(topHBox, simulationSpeedLabel, simulationSpeedSlider);
    }

    private static void setupSlider() {
        simulationSpeedSlider.setMin(0);
        simulationSpeedSlider.setMax(1000);
        simulationSpeedSlider.setValue(500);
        simulationSpeedSlider.setBlockIncrement(1);
        simulationSpeedSlider.setShowTickMarks(true);
        simulationSpeedSlider.setMinorTickCount(10);
        simulationSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                VehicleController.setSleepTimeInMillis(1100 - newValue.longValue());
            }
        });
    }

    private static void setupMenus() {
        civilVehicleMenu.getItems().addAll(civilCarItem, civilMotorcycleItem, civilVanItem);
        policeVehicleMenu.getItems().addAll(policeCarItem, policeMotorcycleItem, policeVanItem);
        sanitaryVehicleMenu.getItems().addAll(sanitaryCarItem, sanitaryVanItem);
        firefightingVehicleMenu.getItems().addAll(firefightingVanItem);

        addVehicleMenu.getItems().addAll(civilVehicleMenu, policeVehicleMenu, sanitaryVehicleMenu, firefightingVehicleMenu);

        menuBar.getMenus().add(addVehicleMenu);

        civilCarItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.CIVIL_CAR, false, false));
        civilMotorcycleItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.CIVIL_MOTORCYCLE, false, false));
        civilVanItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.CIVIL_VAN, false, false));
        policeCarItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.POLICE_CAR, false, false));
        policeMotorcycleItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.POLICE_MOTORCYCLE, false, false));
        policeVanItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.POLICE_VAN, false, false));
        sanitaryCarItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.SANITARY_CAR, false, false));
        sanitaryVanItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.SANITARY_VAN, false, false));
        firefightingVanItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.FIREFIGHTING_VAN, false, false));
    }

    private static void setupButtons() {
        addNewRandomVehicleButton.setOnAction(event -> {
            if (Garage.getVehicleHashMap().size() == Garage.getPlatforms().size() * 28) {
                PopUp.displayWarningInfo(
                        "Warning",
                        "Garage is full",
                        "Cannot add any more vehicles until some of the existing leave."
                );
            } else {
                new Thread(() -> {
                    addNewRandomVehicleButton.setDisable(true);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interrupted) {
                        GarageLogger.log(Level.WARNING, "thread interrupted", interrupted);
                    }
                    addNewRandomVehicleButton.setDisable(false);
                }).start();
                Vehicle vehicle = RandomVehicleGenerator.generateNewRandomVehicle();

                Garage.getPlatforms().get(0).getSpotAt(0, 1).place(vehicle);

                VehicleController controller = new VehicleController(vehicle, MovementTarget.PARKING_SPOT);
                controller.start();

                Garage.getVehicleHashMap().put(vehicle.getRegistrationNumber(), vehicle);
            }
        });

        forceVehiclesToExitButton.setOnAction(event -> {
            synchronized (VehicleController.getJumpToNextSpotLocker()) {
                if (Garage.getVehicleHashMap().size() != 0) {
                    int toExit = (int) (Garage.getVehicleHashMap().size() * 0.15) + 1;
                    ArrayList<Vehicle> vehicles = new ArrayList<>(Garage.getVehicleHashMap().values());
                    while (toExit > 0) {
                        int index = ThreadLocalRandom.current().nextInt(vehicles.size());
                        if (!Emergency.involved(vehicles.get(index))) {
                            boolean alreadyMoving = false;
                            for (VehicleController controller : VehicleController.movingVehicles) {
                                if (controller.getControlledVehicle().equals(vehicles.get(index))) {
                                    controller.setTarget(MovementTarget.EXIT);
                                    alreadyMoving = true;
                                    vehicles.remove(index);
                                    break;
                                }
                            }
                            if (!alreadyMoving) {
                                new VehicleController(vehicles.get(index), MovementTarget.EXIT).start();
                                vehicles.remove(index);
                            }
                            --toExit;
                        }
                    }
                }
            }
        });

        switchToAdminAppButton.setOnAction(event -> {
            if (VehicleController.movingVehicles.size() == 0 || VehicleController.movingVehicles.size() == 1) {
                if (VehicleController.movingVehicles.size() == 1)
                    VehicleController.movingVehicles.get(0).setFinished(true);
                Garage.setObservableLists();
                if (AdminAppHome.platformComboBox.getSelectionModel().getSelectedItem() != null)
                    AdminAppHome.vehicleTable.setItems(
                            AdminAppHome.platformComboBox.getSelectionModel().getSelectedItem().observableListOfVehicles
                    );
                Emergency.happening = false;
                AdminAppHome.stage.show();
                stage.hide();
            } else {
                PopUp.displayWarningInfo(
                        "Warning",
                        "Simulation is running",
                        "Please wait for the simulation to end before switching to Admin app."
                );
            }
        });
    }

    private static void setupGrid() {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20, 0, 0, 0));
        for (int x = 0; x < GRID_WIDTH; ++x) {
            for (int y = 0; y < GRID_LENGTH; ++y) {
                setupField(x, y);
                gridPane.add(fieldMatrix[x][y], x, y);
            }
        }
    }

    private static void setupField(int x, int y) {
        Label label = new Label();
        label.setFont(Font.font("Arial", 30));
        label.setAlignment(Pos.CENTER);
        fieldMatrix[x][y] = new StackPane();
        fieldMatrix[x][y].setAlignment(Pos.CENTER);
        fieldMatrix[x][y].setPadding(new Insets(2));
        fieldMatrix[x][y].getChildren().add(label);
        fieldMatrix[x][y].prefWidthProperty().bind(stage.widthProperty().divide(8).subtract(10));
        fieldMatrix[x][y].prefHeightProperty().bind(stage.heightProperty().divide(10).subtract(10));
        label.textProperty().bind(platformComboBox.getValue().getSpotAt(x, y).getLabel().textProperty());
        if (!platformComboBox.getValue().getSpotAt(x, y).isParkingSpot())
            fieldMatrix[x][y].setBackground(new Background(
                    new BackgroundFill(Color.LIGHTSTEELBLUE, CornerRadii.EMPTY, new Insets(2))
            ));
        else
            fieldMatrix[x][y].setBackground(new Background(
                    new BackgroundFill(Color.STEELBLUE, CornerRadii.EMPTY, new Insets(2))
            ));
    }

    private static void setupBottom() {
        platformComboBox.getItems().addAll(Garage.getPlatforms());
        platformComboBox.setValue(Garage.getPlatforms().get(0));

        platformComboBox.setOnAction(event -> {
            for (int x = 0; x < GRID_WIDTH; ++x)
                for (int y = 0; y < GRID_LENGTH; ++y)
                    ((Label) fieldMatrix[x][y].getChildren().get(0)).textProperty().bind(
                            platformComboBox.getValue().getSpotAt(x, y).getLabel().textProperty()
                    );
        });

        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(8));
        bottomBox.getChildren().add(platformComboBox);
    }

}
