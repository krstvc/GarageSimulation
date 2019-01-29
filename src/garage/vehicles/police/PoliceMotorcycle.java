package garage.vehicles.police;

import garage.vehicles.VehicleType;
import garage.vehicles.civil.Motorcycle;

import java.io.File;

public class PoliceMotorcycle extends Motorcycle implements Police {

    public PoliceMotorcycle() {
        super();
        File defaultImage = new File(
                System.getProperty("user.home") + File.separatorChar
                        + "Documents" + File.separatorChar
                        + "garage files" + File.separatorChar
                        + "images" + File.separatorChar + "PoliceMotorcycle.jpg");
        if (defaultImage.exists())
            setImageURI(defaultImage.toURI().toString());
        else
            setImageURI(new File("non-existent-default-image").toURI().toString());
        setLabel("P");
        setType(VehicleType.POLICE_MOTORCYCLE);
    }

    public PoliceMotorcycle(
            String name,
            String chassisNumber,
            String engineNumber,
            String registrationNumber,
            String imageURI
    ) {
        super(name, chassisNumber, engineNumber, registrationNumber, imageURI);
        setLabel("P");
        setType(VehicleType.POLICE_MOTORCYCLE);
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
