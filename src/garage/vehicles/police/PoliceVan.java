package garage.vehicles.police;

import garage.vehicles.VehicleType;
import garage.vehicles.civil.Van;

import java.io.File;

public class PoliceVan extends Van implements Police {

    public PoliceVan() {
        super();
        File defaultImage = new File(
                System.getProperty("user.home") + File.separatorChar
                        + "Documents" + File.separatorChar
                        + "garage files" + File.separatorChar
                        + "images" + File.separatorChar + "PoliceVan.jpg");
        if (defaultImage.exists())
            setImageURI(defaultImage.toURI().toString());
        else
            setImageURI(new File("non-existent-default-image").toURI().toString());
        setLabel("P");
        setType(VehicleType.POLICE_VAN);
    }

    public PoliceVan(
            String name,
            String chassisNumber,
            String engineNumber,
            String registrationNumber,
            String imageURI,
            double loadCapacity
    ) {
        super(name, chassisNumber, engineNumber, registrationNumber, imageURI, loadCapacity);
        setLabel("P");
        setType(VehicleType.POLICE_VAN);
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
