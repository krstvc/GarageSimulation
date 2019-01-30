package garage;

import garage.vehicles.Vehicle;
import garage.vehicles.firefighting.Firefighting;
import garage.vehicles.police.Police;
import garage.vehicles.sanitary.Sanitary;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Platform implements Serializable {

    private final static int PLATFORM_WIDTH = 8;
    private final static int PLATFORM_LENGTH = 10;

    private static int COUNTER = 0;

    private int platformIndex;

    private ArrayList<GarageSpot> spots;
    private ArrayList<GarageSpot> parkingSpots;
    private ArrayList<GarageSpot> freeParkingSpots;

    public transient ObservableList<Vehicle> observableListOfVehicles;

    private transient Object locker = new Object();


    public Platform() {
        spots = new ArrayList<>();
        parkingSpots = new ArrayList<>();
        freeParkingSpots = new ArrayList<>();

        for (int x = 0; x < PLATFORM_WIDTH; ++x) {
            for (int y = 0; y < PLATFORM_LENGTH; ++y) {
                if (locationIntendedForParking(x, y)) {
                    GarageSpot newSpot = new GarageSpot(x, y, true, this);
                    spots.add(newSpot);
                    parkingSpots.add(newSpot);
                    freeParkingSpots.add(newSpot);
                } else
                    spots.add(new GarageSpot(x, y, false, this));
            }
        }

        platformIndex = COUNTER++;
    }

    public void findRandomPositionAndPark(Vehicle vehicle) {
        Random random = new Random();
        GarageSpot location = freeParkingSpots.get(random.nextInt(freeParkingSpots.size()));
        location.place(vehicle);
        freeParkingSpots.remove(location);
        Garage.getVehicleHashMap().put(vehicle.getRegistrationNumber(), vehicle);
        if (!(vehicle instanceof Police) && !(vehicle instanceof Sanitary) && !(vehicle instanceof Firefighting))
            Garage.getPaymentEvidenceHashMap().put(vehicle.getRegistrationNumber(), LocalDateTime.now());
        observableListOfVehicles.add(vehicle);
    }

    public void removeVehicleFromGarage(Vehicle vehicle) {
        GarageSpot location = getSpotAt(vehicle.getLocation().getxCoordinate(), vehicle.getLocation().getyCoordinate());
        location.leave();
        freeParkingSpots.add(location);
        Garage.getVehicleHashMap().remove(vehicle.getRegistrationNumber());
        if (!(vehicle instanceof Police) && !(vehicle instanceof Sanitary) && !(vehicle instanceof Firefighting))
            Garage.chargeOnExit(vehicle);
        observableListOfVehicles.remove(vehicle);
    }

    public static boolean locationIntendedForParking(int x, int y) {
        return ((x == 0 || x == 7) && (y > 1)) || ((x == 3 || x == 4) && (y > 1 && y < 8));
    }

    public boolean isFull() {
        return freeParkingSpots.size() == 0;
    }

    public int getPlatformIndex() {
        return platformIndex;
    }

    public void setPlatformIndex(int platformIndex) {
        this.platformIndex = platformIndex;
    }

    public ArrayList<GarageSpot> getSpots() {
        return spots;
    }

    public ArrayList<GarageSpot> getFreeParkingSpots() {
        return freeParkingSpots;
    }

    public GarageSpot getSpotAt(int x, int y) {
        return spots.get(x * PLATFORM_LENGTH + y);
    }

    @Override
    public String toString() {
        return "Platform " + (platformIndex + 1);
    }

    public Object getLocker() {
        return locker;
    }
}
