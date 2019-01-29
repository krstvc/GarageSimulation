package garage.ui;

import garage.Garage;
import garage.Platform;
import garage.utility.logging.GarageLogger;
import garage.vehicles.Vehicle;
import garage.vehicles.VehicleType;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;

public class AdminAppHome {

    public static Stage stage;
    public static Scene scene;
    public static BorderPane layout;

    public static VBox top = new VBox(8);
    public static VBox bottom = new VBox(8);
    public static HBox buttonBox = new HBox(8);
    public static HBox editAndDeleteBox = new HBox(8);
    public static StackPane centerPane = new StackPane();
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

    public static Button switchToUserAppButton = new Button("Switch to user app");
    public static Button editVehicleButton = new Button("Edit vehicle");
    public static Button deleteVehicleButton = new Button("Delete vehicle");

    public static TableView<Vehicle> vehicleTable = new TableView<>();
    public static TableColumn<Vehicle, String> nameColumn = new TableColumn<>("Vehicle name");
    public static TableColumn<Vehicle, String> chassisNumberColumn = new TableColumn<>("Chassis number");
    public static TableColumn<Vehicle, String> engineNumberColumn = new TableColumn<>("Engine number");
    public static TableColumn<Vehicle, String> registrationNumberColumn = new TableColumn<>("Registration number");
    public static TableColumn<Vehicle, String> imageURIcolumn = new TableColumn<>("Image");

    public static ComboBox<Platform> platformComboBox = new ComboBox<>();
    public static Label platformLabel = new Label("Number of platforms: " + Garage.getPlatforms().size());

    public static void initialize() {
        //Setting up top
        setupMenus();
        setupTopButtons();
        top.setAlignment(Pos.CENTER);
        top.getChildren().addAll(menuBar, buttonBox);

        //Setting up center
        setupTable();

        //Setting up bottom
        setupPlatformComboBox();
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(8));
        bottom.getChildren().addAll(platformLabel, platformComboBox);

        //Window setup
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        layout = new BorderPane(centerPane, top, null, bottom, null);
        scene = new Scene(layout, screenSize.width / 1.3, screenSize.height / 1.2, Color.ANTIQUEWHITE);
        stage = new Stage();
        stage.setMinWidth(screenSize.width / 2);
        stage.setMinHeight(screenSize.height / 2);

        createBindings();

        stage.setTitle("Garage | Admin app");
        stage.setScene(scene);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    Garage.writeToSerializationFile();
                } catch (IOException exception) {
                    GarageLogger.log(Level.WARNING, "unable to write to serialization file", exception);
                }
                javafx.application.Platform.exit();
            }
        });
        stage.show();
    }

    private static void setupMenus() {
        civilVehicleMenu.getItems().addAll(civilCarItem, civilMotorcycleItem, civilVanItem);
        policeVehicleMenu.getItems().addAll(policeCarItem, policeMotorcycleItem, policeVanItem);
        sanitaryVehicleMenu.getItems().addAll(sanitaryCarItem, sanitaryVanItem);
        firefightingVehicleMenu.getItems().addAll(firefightingVanItem);

        addVehicleMenu.getItems().addAll(civilVehicleMenu, policeVehicleMenu, sanitaryVehicleMenu, firefightingVehicleMenu);

        menuBar.getMenus().add(addVehicleMenu);

        civilCarItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.CIVIL_CAR, false));
        civilMotorcycleItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.CIVIL_MOTORCYCLE, false));
        civilVanItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.CIVIL_VAN, false));
        policeCarItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.POLICE_CAR, false));
        policeMotorcycleItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.POLICE_MOTORCYCLE, false));
        policeVanItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.POLICE_VAN, false));
        sanitaryCarItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.SANITARY_CAR, false));
        sanitaryVanItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.SANITARY_VAN, false));
        firefightingVanItem.setOnAction(event -> NewVehicleWindow.initialize(VehicleType.FIREFIGHTING_VAN, false));
    }

    private static void setupTopButtons() {
        editAndDeleteBox.setAlignment(Pos.CENTER_LEFT);
        editAndDeleteBox.getChildren().addAll(editVehicleButton, deleteVehicleButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(2, 8, 8, 8));
        buttonBox.getChildren().addAll(editAndDeleteBox, switchToUserAppButton);

        HBox.setHgrow(editAndDeleteBox, Priority.ALWAYS);
        HBox.setHgrow(switchToUserAppButton, Priority.NEVER);

        editVehicleButton.setOnAction(event -> NewVehicleWindow.initialize(
                vehicleTable.getSelectionModel().getSelectedItem().getType(), true
        ));

        deleteVehicleButton.setOnAction(event -> {
            Vehicle toDelete = vehicleTable.getSelectionModel().getSelectedItem();
            Platform platform = platformComboBox.getValue();
            platform.removeVehicleFromGarage(toDelete);
        });

        switchToUserAppButton.setOnAction(event -> UserAppSetup.initialize());
    }

    private static void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("name"));
        chassisNumberColumn.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("chassisNumber"));
        engineNumberColumn.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("engineNumber"));
        registrationNumberColumn.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("registrationNumber"));
        imageURIcolumn.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("imageURI"));

        vehicleTable.getColumns().addAll(nameColumn, chassisNumberColumn, engineNumberColumn, registrationNumberColumn, imageURIcolumn);
        vehicleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        vehicleTable.setPadding(new Insets(8, 0, 8, 0));

        centerPane.setAlignment(Pos.CENTER);
        centerPane.setPadding(new Insets(0, 8, 0, 8));
        centerPane.getChildren().add(vehicleTable);
    }

    private static void setupPlatformComboBox() {
        platformComboBox.getItems().addAll(Garage.getPlatforms());
        platformComboBox.setPromptText("Select a platform");

        platformComboBox.setOnAction(event -> vehicleTable.setItems(platformComboBox.getValue().observableListOfVehicles));
    }

    private static void createBindings() {
        nameColumn.prefWidthProperty().bind(scene.widthProperty().divide(5));
        chassisNumberColumn.prefWidthProperty().bind(scene.widthProperty().divide(5));
        engineNumberColumn.prefWidthProperty().bind(scene.widthProperty().divide(5));
        registrationNumberColumn.prefWidthProperty().bind(scene.widthProperty().divide(5));
        imageURIcolumn.prefWidthProperty().bind(scene.widthProperty().divide(5));

        nameColumn.minWidthProperty().bind(scene.widthProperty().divide(10));
        chassisNumberColumn.minWidthProperty().bind(scene.widthProperty().divide(10));
        engineNumberColumn.minWidthProperty().bind(scene.widthProperty().divide(10));
        registrationNumberColumn.minWidthProperty().bind(scene.widthProperty().divide(10));
        imageURIcolumn.minWidthProperty().bind(scene.widthProperty().divide(10));

        editVehicleButton.disableProperty().bind(Bindings.size(vehicleTable.getSelectionModel().getSelectedItems()).isNotEqualTo(1));
        deleteVehicleButton.disableProperty().bind(Bindings.size(vehicleTable.getSelectionModel().getSelectedItems()).isNotEqualTo(1));

        menuBar.disableProperty().bind(
                platformComboBox.getSelectionModel().selectedItemProperty().isNull()
                        .or(Bindings.size(vehicleTable.getItems()).greaterThanOrEqualTo(28)));
    }

}
