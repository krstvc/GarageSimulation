package garage.vehicles.police;

import garage.vehicles.VehicleType;
import garage.vehicles.civil.Car;
import javafx.application.Platform;

import java.io.File;

public class PoliceCar extends Car implements Police {

    public PoliceCar() {
        super();
        File defaultImage = new File(
                System.getProperty("user.home") + File.separatorChar
                        + "Documents" + File.separatorChar
                        + "garage files" + File.separatorChar
                        + "images" + File.separatorChar + "PoliceCar.jpg");
        if (defaultImage.exists())
            setImageURI(defaultImage.toURI().toString());
        else
            setImageURI(new File("non-existent-default-image").toURI().toString());
        setLabel("P");
        setType(VehicleType.POLICE_CAR);
    }

    public PoliceCar(
            String name,
            String chassisNumber,
            String engineNumber,
            String registrationNumber,
            String imageURI,
            int numberOfDoors
    ) {
        super(name, chassisNumber, engineNumber, registrationNumber, imageURI, numberOfDoors);
        setLabel("P");
        setType(VehicleType.POLICE_CAR);
    }

    @Override
    public void turnOnRotation() {
        setLabel("PR");
    }

    @Override
    public void turnOffRotation() {
        setLabel("P");
    }
}
