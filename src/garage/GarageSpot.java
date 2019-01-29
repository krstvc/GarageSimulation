package garage;

import garage.vehicles.Vehicle;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GarageSpot implements Serializable {

    private Vehicle vehicle;
    private Platform platform;

    private boolean isParkingSpot;
    private boolean isFree;

    private int xCoordinate;
    private int yCoordinate;

    private String defaultLabel;
    private transient Label label;

    private GarageSpot toTraverseCurrentPlatform;
    private GarageSpot toNextPlatform;
    private GarageSpot toPreviousPlatform;
    private GarageSpot toParkingSpotNearby;
    private GarageSpot toNeighborSpot;


    public GarageSpot(
            int xCoordinate,
            int yCoordinate,
            boolean isParkingSpot,
            Platform platform
    ) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.isParkingSpot = isParkingSpot;
        this.platform = platform;
        isFree = true;

        if (isParkingSpot)
            defaultLabel = "*";
        else
            defaultLabel = " ";

        setupLabelForUI();
    }


    private void setupLabelForUI() {
        if (vehicle != null)
            label = new Label(vehicle.getLabel());
        else
            label = new Label(defaultLabel);
        label.setFont(Font.font("Arial", 30));
    }

    public void place(Vehicle vehicle) {
        isFree = false;
        setVehicle(vehicle);
        vehicle.setLocation(this);

        if (isParkingSpot)
            vehicle.setParked(true);

        javafx.application.Platform.runLater(() -> setLabel(vehicle.getLabel()));
    }

    public void leave() {
        if (vehicle != null) {
            vehicle.setLocation(null);

            if (isParkingSpot)
                vehicle.setParked(false);

            setVehicle(null);
            isFree = true;

            javafx.application.Platform.runLater(() -> setLabel(defaultLabel));
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        setupLabelForUI();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    //Getters and setters
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public boolean isParkingSpot() {
        return isParkingSpot;
    }

    public void setParkingSpot(boolean parkingSpot) {
        isParkingSpot = parkingSpot;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(String text) {
        label.setText(text);
    }

    public GarageSpot getToTraverseCurrentPlatform() {
        return toTraverseCurrentPlatform;
    }

    public void setToTraverseCurrentPlatform(GarageSpot toTraverseCurrentPlatform) {
        this.toTraverseCurrentPlatform = toTraverseCurrentPlatform;
    }

    public GarageSpot getToNextPlatform() {
        return toNextPlatform;
    }

    public void setToNextPlatform(GarageSpot toNextPlatform) {
        this.toNextPlatform = toNextPlatform;
    }

    public GarageSpot getToPreviousPlatform() {
        return toPreviousPlatform;
    }

    public void setToPreviousPlatform(GarageSpot toPreviousPlatform) {
        this.toPreviousPlatform = toPreviousPlatform;
    }

    public GarageSpot getToParkingSpotNearby() {
        return toParkingSpotNearby;
    }

    public void setToParkingSpotNearby(GarageSpot toParkingSpotNearby) {
        this.toParkingSpotNearby = toParkingSpotNearby;
    }

    public GarageSpot getToNeighborSpot() {
        return toNeighborSpot;
    }

    public void setToNeighborSpot(GarageSpot toNeighborSpot) {
        this.toNeighborSpot = toNeighborSpot;
    }

}
