package garage.vehicles.firefighting;

import garage.vehicles.VehicleType;
import garage.vehicles.civil.Van;
import javafx.application.Platform;

import java.io.File;

public class FirefightingVan extends Van implements Firefighting {

    public FirefightingVan() {
        super();
        File defaultImage = new File(
                System.getProperty("user.home") + File.separatorChar
                        + "Documents" + File.separatorChar
                        + "garage files" + File.separatorChar
                        + "images" + File.separatorChar + "FirefightingVan.jpg");
        if (defaultImage.exists())
            setImageURI(defaultImage.toURI().toString());
        else
            setImageURI(new File("non-existent-default-image").toURI().toString());
        setLabel("F");
        setType(VehicleType.FIREFIGHTING_VAN);
    }

    public FirefightingVan(
            String name,
            String chassisNumber,
            String engineNumber,
            String registrationNumber,
            String imageURI,
            double loadCapacity
    ) {
        super(name, chassisNumber, engineNumber, registrationNumber, imageURI, loadCapacity);
        setLabel("F");
        setType(VehicleType.FIREFIGHTING_VAN);
    }

    @Override
    public void turnOnRotation() {
        setLabel("FR");
    }

    @Override
    public void turnOffRotation() {
        setLabel("F");
    }

}
