package garage.vehicles.sanitary;

import garage.vehicles.VehicleType;
import garage.vehicles.civil.Car;

import java.io.File;

public class SanitaryCar extends Car implements Sanitary {

    public SanitaryCar() {
        super();
        File defaultImage = new File(
                System.getProperty("user.home") + File.separatorChar
                        + "Documents" + File.separatorChar
                        + "garage files" + File.separatorChar
                        + "images" + File.separatorChar + "SanitaryCar.jpg");
        if (defaultImage.exists())
            setImageURI(defaultImage.toURI().toString());
        else
            setImageURI(new File("non-existent-default-image").toURI().toString());
        setLabel("H");
        setType(VehicleType.SANITARY_CAR);
    }

    public SanitaryCar(
            String name,
            String chassisNumber,
            String engineNumber,
            String registrationNumber,
            String imageURI,
            int numberOfDoors
    ) {
        super(name, chassisNumber, engineNumber, registrationNumber, imageURI, numberOfDoors);
        setLabel("H");
        setType(VehicleType.SANITARY_CAR);
    }

    @Override
    public void turnOnRotation() {
        setLabel("HR");
    }

    @Override
    public void turnOffRotation() {
        setLabel("H");
    }
}
