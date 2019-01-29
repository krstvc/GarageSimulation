package garage.vehicles.civil;

import garage.vehicles.Vehicle;
import garage.vehicles.VehicleType;

import java.io.File;

public class Motorcycle extends Vehicle {

    public Motorcycle() {
        super();
        File defaultImage = new File(
                System.getProperty("user.home") + File.separatorChar
                        + "Documents" + File.separatorChar
                        + "garage files" + File.separatorChar
                        + "images" + File.separatorChar + "CivilMotorcycle.jpg");
        if (defaultImage.exists())
            setImageURI(defaultImage.toURI().toString());
        else
            setImageURI(new File("non-existent-default-image").toURI().toString());
        setType(VehicleType.CIVIL_MOTORCYCLE);
    }

    public Motorcycle(
            String name,
            String chassisNumber,
            String engineNumber,
            String registrationNumber,
            String imageURI
    ) {
        super(name, chassisNumber, engineNumber, registrationNumber, imageURI);
        setType(VehicleType.CIVIL_MOTORCYCLE);
    }

}
