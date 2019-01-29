package garage.vehicles.civil;

import garage.vehicles.Vehicle;
import garage.vehicles.VehicleType;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class Van extends Vehicle {

    private double loadCapacity;

    public Van() {
        super();
        loadCapacity = ThreadLocalRandom.current().nextDouble(10000);
        File defaultImage = new File(
                System.getProperty("user.home") + File.separatorChar
                        + "Documents" + File.separatorChar
                        + "garage files" + File.separatorChar
                        + "images" + File.separatorChar + "CivilVan.jpg");
        if (defaultImage.exists())
            setImageURI(defaultImage.toURI().toString());
        else
            setImageURI(new File("non-existent-default-image").toURI().toString());
        setType(VehicleType.CIVIL_VAN);
    }

    public Van(
            String name,
            String chassisNumber,
            String engineNumber,
            String registrationNumber,
            String imageURI,
            double loadCapacity
    ) {
        super(name, chassisNumber, engineNumber, registrationNumber, imageURI);
        this.loadCapacity = loadCapacity;
        setType(VehicleType.CIVIL_VAN);
    }

    public double getLoadCapacity() {
        return loadCapacity;
    }

    public void setLoadCapacity(double loadCapacity) {
        this.loadCapacity = loadCapacity;
    }
}
