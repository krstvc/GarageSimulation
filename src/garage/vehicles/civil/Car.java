package garage.vehicles.civil;

import garage.vehicles.Vehicle;
import garage.vehicles.VehicleType;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class Car extends Vehicle {

    private int numberOfDoors;

    public Car() {
        super();
        numberOfDoors = ThreadLocalRandom.current().nextInt(5);
        File defaultImage = new File(
                System.getProperty("user.home") + File.separatorChar
                        + "Documents" + File.separatorChar
                        + "garage files" + File.separatorChar
                        + "images" + File.separatorChar + "CivilCar.jpg");
        if (defaultImage.exists())
            setImageURI(defaultImage.toURI().toString());
        else
            setImageURI(new File("non-existent-default-image").toURI().toString());
        setType(VehicleType.CIVIL_CAR);
    }

    public Car(
            String name,
            String chassisNumber,
            String engineNumber,
            String registrationNumber,
            String imageURI,
            int numberOfDoors
    ) {
        super(name, chassisNumber, engineNumber, registrationNumber, imageURI);
        this.numberOfDoors = numberOfDoors;
        setType(VehicleType.CIVIL_CAR);
    }

    public int getNumberOfDoors() {
        return numberOfDoors;
    }

    public void setNumberOfDoors(int numberOfDoors) {
        this.numberOfDoors = numberOfDoors;
    }
}
