package garage.vehicles;

import garage.Garage;
import garage.GarageSpot;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Vehicle implements Serializable {

    private String name;
    private String chassisNumber;
    private String engineNumber;
    private String registrationNumber;
    private String imageURI;

    private String label;
    private VehicleType type;

    private GarageSpot location;
    private boolean isParked;

    public Vehicle() {
        int id;
        do {
            id = ThreadLocalRandom.current().nextInt(1000);
        } while (Garage.getVehicleHashMap().containsKey("RandGenChassis" + id));

        name = "RandGenName" + id;
        chassisNumber = "RandGenChassis" + id;
        engineNumber = "RandGenEngine" + id;
        registrationNumber = "RandGenRegistration" + id;
        imageURI = "TODO";  //todo

        label = "V";
        location = null;
    }

    public Vehicle(
            String name,
            String chassisNumber,
            String engineNumber,
            String registrationNumber,
            String imageURI
    ) {
        this.name = name;
        this.chassisNumber = chassisNumber;
        this.engineNumber = engineNumber;
        this.registrationNumber = registrationNumber;
        this.imageURI = imageURI;

        label = "V";
        location = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public GarageSpot getLocation() {
        return location;
    }

    public void setLocation(GarageSpot location) {
        this.location = location;
    }

    public boolean isParked() {
        return isParked;
    }

    public void setParked(boolean parked) {
        isParked = parked;
    }

}
