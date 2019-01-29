package garage.vehicles.sanitary;

import garage.vehicles.VehicleType;
import garage.vehicles.civil.Van;

import java.io.File;

public class SanitaryVan extends Van implements Sanitary {

    public SanitaryVan() {
        super();
        File defaultImage = new File(
                System.getProperty("user.home") + File.separatorChar
                        + "Documents" + File.separatorChar
                        + "garage files" + File.separatorChar
                        + "images" + File.separatorChar + "SanitaryVan.jpg");
        if (defaultImage.exists())
            setImageURI(defaultImage.toURI().toString());
        else
            setImageURI(new File("non-existent-default-image").toURI().toString());
        setLabel("H");
        setType(VehicleType.SANITARY_VAN);
    }

    public SanitaryVan(
            String name,
            String chassisNumber,
            String engineNumber,
            String registrationNumber,
            String imageURI,
            double loadCapacity
    ) {
        super(name, chassisNumber, engineNumber, registrationNumber, imageURI, loadCapacity);
        setLabel("H");
        setType(VehicleType.SANITARY_VAN);
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
