package garage.ui;

import garage.Garage;
import garage.simulation.control.MovementTarget;
import garage.simulation.control.VehicleController;
import garage.vehicles.Vehicle;
import garage.vehicles.VehicleType;
import garage.vehicles.civil.Car;
import garage.vehicles.civil.Motorcycle;
import garage.vehicles.civil.Van;
import garage.vehicles.firefighting.FirefightingVan;
import garage.vehicles.police.PoliceCar;
import garage.vehicles.police.PoliceMotorcycle;
import garage.vehicles.police.PoliceVan;
import garage.vehicles.sanitary.SanitaryCar;
import garage.vehicles.sanitary.SanitaryVan;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;

public class NewVehicleWindow {

    public static Stage stage;
    public static Scene scene;
    public static GridPane layout = new GridPane();

    public static Label nameLabel = new Label("Vehicle name");
    public static Label chassisLabel = new Label("Chassis number");
    public static Label engineLabel = new Label("Engine number");
    public static Label registrationLabel = new Label("Registration number");
    public static Label imageLabel = new Label("Image");
    public static Label doorsLabel = new Label("Number of doors");
    public static Label capacityLabel = new Label("Load capacity");
    public static Label imageURILabel = new Label("");

    public static TextField nameInput = new TextField();
    public static TextField chassisInput = new TextField();
    public static TextField engineInput = new TextField();
    public static TextField registrationInput = new TextField();
    public static TextField doorsInput = new TextField();
    public static TextField capacityInput = new TextField();

    public static Button imageButton = new Button("Choose");
    public static Button viewButton = new Button("View");
    public static Button submitButton = new Button("Submit");

    public static VehicleType typeFlag;
    public static boolean toEdit;
    public static boolean adminApp;        //true for admin app, false for user app

    public static void initialize(VehicleType type, boolean edit, boolean admin) {
        typeFlag = type;
        toEdit = edit;
        adminApp = admin;

        formGrid();
        setupButtons();

        if (toEdit == true)
            updateFields();
        else
            clearFields();

        if (stage == null) {
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Garage | Add new vehicle");
            stage.setResizable(false);
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        scene = new Scene(layout, screenSize.width / 2.5, screenSize.height / 1.8);
        stage.setScene(scene);
        stage.show();
    }

    public static void formGrid() {
        layout = new GridPane();
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(12));
        layout.setVgap(8);
        layout.setHgap(12);

        GridPane.setConstraints(nameLabel, 0, 0);
        layout.getChildren().add(nameLabel);

        GridPane.setConstraints(nameInput, 1, 0);
        layout.getChildren().add(nameInput);

        GridPane.setConstraints(chassisLabel, 0, 1);
        layout.getChildren().add(chassisLabel);

        GridPane.setConstraints(chassisInput, 1, 1);
        layout.getChildren().add(chassisInput);

        GridPane.setConstraints(engineLabel, 0, 2);
        layout.getChildren().add(engineLabel);

        GridPane.setConstraints(engineInput, 1, 2);
        layout.getChildren().add(engineInput);

        GridPane.setConstraints(registrationLabel, 0, 3);
        layout.getChildren().add(registrationLabel);

        GridPane.setConstraints(registrationInput, 1, 3);
        layout.getChildren().add(registrationInput);

        if (isCarSelected(typeFlag)) {
            GridPane.setConstraints(doorsLabel, 0, 4);
            layout.getChildren().add(doorsLabel);

            GridPane.setConstraints(doorsInput, 1, 4);
            layout.getChildren().add(doorsInput);

            doorsInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*"))
                        doorsInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
        } else if (isVanSelected(typeFlag)) {
            GridPane.setConstraints(capacityLabel, 0, 4);
            layout.getChildren().add(capacityLabel);

            GridPane.setConstraints(capacityInput, 1, 4);
            layout.getChildren().add(capacityInput);

            capacityInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("(\\d+(\\.\\d*)?)?"))
                        capacityInput.setText(oldValue);
                }
            });
        }

        GridPane.setConstraints(imageLabel, 0, 6);
        layout.getChildren().add(imageLabel);

        GridPane.setConstraints(imageButton, 1, 6);
        layout.getChildren().addAll(imageButton);

        GridPane.setConstraints(viewButton, 1, 7);
        layout.getChildren().add(viewButton);

        GridPane.setConstraints(submitButton, 1, 11);
        layout.getChildren().add(submitButton);
    }

    private static void clearFields() {
        nameInput.clear();
        chassisInput.clear();
        engineInput.clear();
        registrationInput.clear();
        imageURILabel.setText("");
        doorsInput.clear();
        capacityInput.clear();
    }

    private static void updateFields() {
        Vehicle selectedVehicle = AdminAppHome.vehicleTable.getSelectionModel().getSelectedItem();
        nameInput.setText(selectedVehicle.getName());
        chassisInput.setText(selectedVehicle.getChassisNumber());
        engineInput.setText(selectedVehicle.getEngineNumber());
        registrationInput.setText(selectedVehicle.getRegistrationNumber());
        imageURILabel.setText(selectedVehicle.getImageURI());
        if (isCarSelected(selectedVehicle.getType())) {
            Car selectedCar = (Car) selectedVehicle;
            doorsInput.setText("" + selectedCar.getNumberOfDoors());
        } else if (isVanSelected(selectedVehicle.getType())) {
            Van selectedVan = (Van) selectedVehicle;
            capacityInput.setText("" + selectedVan.getLoadCapacity());
        }
    }

    private static boolean isCarSelected(VehicleType vehicleType) {
        return vehicleType == VehicleType.CIVIL_CAR ||
                vehicleType == VehicleType.POLICE_CAR ||
                vehicleType == VehicleType.SANITARY_CAR;
    }

    private static boolean isMotorcycleSelected(VehicleType vehicleType) {
        return vehicleType == VehicleType.CIVIL_MOTORCYCLE ||
                vehicleType == VehicleType.POLICE_MOTORCYCLE;
    }

    private static boolean isVanSelected(VehicleType vehicleType) {
        return vehicleType == VehicleType.CIVIL_VAN ||
                vehicleType == VehicleType.POLICE_VAN ||
                vehicleType == VehicleType.SANITARY_VAN ||
                vehicleType == VehicleType.FIREFIGHTING_VAN;
    }

    private static void setupButtons() {
        BooleanBinding notAllFieldsFilledProperty = Bindings.isEmpty(nameInput.textProperty())
                .or(Bindings.isEmpty(chassisInput.textProperty())
                        .or(Bindings.isEmpty(engineInput.textProperty())
                                .or(Bindings.isEmpty(registrationInput.textProperty()))));
        if (isCarSelected(typeFlag))
            notAllFieldsFilledProperty = notAllFieldsFilledProperty.or(Bindings.isEmpty(doorsInput.textProperty()));
        else if (isVanSelected(typeFlag))
            notAllFieldsFilledProperty = notAllFieldsFilledProperty.or(Bindings.isEmpty(capacityInput.textProperty()));

        BooleanBinding imageNotSelectedProperty = Bindings.isEmpty(imageURILabel.textProperty());

        //setting up button for choosing images
        imageButton.setOnAction(event -> chooseImage());
        imageButton.setPrefWidth(100);

        //setting up button for viewing images
        viewButton.setOnAction(event -> viewImage());
        viewButton.setPrefWidth(100);
        viewButton.disableProperty().bind(imageNotSelectedProperty);

        //setting up button for submitting info and adding new vehicle to the garage
        submitButton.setOnAction(event -> setupSubmitButton());
        submitButton.setPrefWidth(100);
        submitButton.disableProperty().bind(notAllFieldsFilledProperty.or(imageNotSelectedProperty));
    }

    private static void setupSubmitButton() {
        if ((isCarSelected(typeFlag) && validDoorsInput()) || (isVanSelected(typeFlag) && validCapacityInput()) ||
                isMotorcycleSelected(typeFlag)) {
            if (toEdit == true)
                updateCurrentVehicle();
            else
                formNewVehicle();
            stage.close();
        }
    }

    private static void updateCurrentVehicle() {
        Vehicle currentVehicle = AdminAppHome.vehicleTable.getSelectionModel().getSelectedItem();
        currentVehicle.setName(nameInput.getText());
        currentVehicle.setChassisNumber(chassisInput.getText());
        currentVehicle.setEngineNumber(engineInput.getText());
        currentVehicle.setRegistrationNumber(registrationInput.getText());
        currentVehicle.setImageURI(imageURILabel.getText());
        switch (typeFlag) {
            case CIVIL_CAR:
            case POLICE_CAR:
            case SANITARY_CAR:
                Car currentCar = (Car) currentVehicle;
                currentCar.setNumberOfDoors(Integer.parseInt(doorsInput.getText()));
            case CIVIL_VAN:
            case POLICE_VAN:
            case SANITARY_VAN:
            case FIREFIGHTING_VAN:
                Van currentVan = (Van) currentVehicle;
                currentVan.setLoadCapacity(Double.parseDouble(capacityInput.getText()));
        }
        AdminAppHome.vehicleTable.refresh();
    }

    private static void formNewVehicle() {
        Vehicle vehicle = null;
        String name = nameInput.getText();
        String chassis = chassisInput.getText();
        String engine = engineInput.getText();
        String registration = registrationInput.getText();
        String image = imageURILabel.getText();
        int doors;
        double load;
        switch (typeFlag) {
            case CIVIL_CAR:
                doors = Integer.parseInt(doorsInput.getText());
                vehicle = new Car(name, chassis, engine, registration, image, doors);
                break;
            case CIVIL_MOTORCYCLE:
                vehicle = new Motorcycle(name, chassis, engine, registration, image);
                break;
            case CIVIL_VAN:
                load = Double.parseDouble(capacityInput.getText());
                vehicle = new Van(name, chassis, engine, registration, image, load);
                break;
            case POLICE_CAR:
                doors = Integer.parseInt(doorsInput.getText());
                vehicle = new PoliceCar(name, chassis, engine, registration, image, doors);
                break;
            case POLICE_MOTORCYCLE:
                vehicle = new PoliceMotorcycle(name, chassis, engine, registration, image);
                break;
            case POLICE_VAN:
                load = Double.parseDouble(capacityInput.getText());
                vehicle = new PoliceVan(name, chassis, engine, registration, image, load);
                break;
            case SANITARY_CAR:
                doors = Integer.parseInt(doorsInput.getText());
                vehicle = new SanitaryCar(name, chassis, engine, registration, image, doors);
                break;
            case SANITARY_VAN:
                load = Double.parseDouble(capacityInput.getText());
                vehicle = new SanitaryVan(name, chassis, engine, registration, image, load);
                break;
            case FIREFIGHTING_VAN:
                load = Double.parseDouble(capacityInput.getText());
                vehicle = new FirefightingVan(name, chassis, engine, registration, image, load);
        }
        if (adminApp) {
            AdminAppHome.platformComboBox.getSelectionModel().getSelectedItem().findRandomPositionAndPark(vehicle);
        } else {
            Garage.getPlatforms().get(0).getSpotAt(0, 1).place(vehicle);
            new VehicleController(vehicle, MovementTarget.PARKING_SPOT).start();
        }
    }

    private static void chooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a vehicle image");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
        );
        File file = chooser.showOpenDialog(new Stage());

        if (file != null) {
            imageURILabel.setText(file.toURI().toString());
        }
    }

    private static void viewImage() {
        if (!imageURILabel.getText().equals("")) {
            Stage stage = new Stage();
            File imageFile = new File(imageURILabel.getText());
            Image image = new Image(imageFile.toString());
            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true);
            imageView.setImage(image);
            imageView.fitHeightProperty().bind(stage.heightProperty().subtract(40));
            imageView.fitWidthProperty().bind(stage.widthProperty().subtract(40));


            VBox layout = new VBox();
            layout.setPadding(new Insets(20));
            layout.setAlignment(Pos.CENTER);
            layout.getChildren().add(imageView);
            stage.setTitle("Vehicle image");
            Scene scene = new Scene(layout, 620, 460);
            stage.setScene(scene);
            stage.show();
        }
    }

    private static boolean validDoorsInput() {
        try {
            int result = Integer.parseInt(doorsInput.getText());
            if (result > 0)
                return true;
            throw new NumberFormatException();
        } catch (Exception e) {
            doorsInput.clear();
            String title = "Warning";
            String headerText = "Type mismatch";
            String contentText = "Number of doors must be a positive integer.";
            PopUp.displayWarningInfo(title, headerText, contentText);
            return false;
        }
    }

    private static boolean validCapacityInput() {
        try {
            double result = Double.parseDouble(capacityInput.getText());
            if (result > 0)
                return true;
            throw new NumberFormatException();
        } catch (Exception e) {
            capacityInput.clear();
            String title = "Warning";
            String headerText = "Type mismatch";
            String contentText = "Load capacity must be a positive real number.";
            PopUp.displayWarningInfo(title, headerText, contentText);
            return false;
        }
    }

}
