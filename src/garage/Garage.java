package garage;

import garage.utility.GarageProperties;
import garage.utility.logging.GarageLogger;
import garage.vehicles.Vehicle;
import javafx.collections.FXCollections;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Garage {

    private static File SERIALIZATION_FILE = new File(System.getProperty("user.home") + File.separatorChar
            + "Documents" + File.separatorChar + "garage files" + File.separatorChar + "garage.ser");

    private static ArrayList<Platform> platforms;
    private static HashMap<String, Vehicle> vehicleHashMap;

    private static final Object vehicleHashMapLocker = new Object();


    public static void setupGarage() throws Exception {
        //Setting up required files first
        GarageLogger.setupLogger();
        GarageProperties.setupProperties();

        if (SERIALIZATION_FILE.exists()) {
            try {
                readFromSerializationFile();                //Try to read from serialization file if possible
            } catch (ClassNotFoundException exception) {
                setupNewGarage();                           //Create new garage if the file is damaged
            }
        } else
            setupNewGarage();                               //Create new garage if there is no serialization file

        setObservableLists();
    }

    private static void readFromSerializationFile() throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(SERIALIZATION_FILE));
        platforms = (ArrayList<Platform>) stream.readObject();
        vehicleHashMap = (HashMap<String, Vehicle>) stream.readObject();
        stream.close();
    }

    public static void writeToSerializationFile() throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FILE));
        stream.writeObject(platforms);
        stream.writeObject(vehicleHashMap);
        stream.close();
    }

    private static void setupNewGarage() {
        platforms = new ArrayList<>();
        for (int i = 0; i < GarageProperties.getNumberOfPlatformsValue(); ++i)
            platforms.add(new Platform());
        vehicleHashMap = new HashMap<>();
    }

    public static void setObservableLists() {
        for (Platform platform : platforms) {
            platform.observableListOfVehicles = FXCollections.observableArrayList();
            vehicleHashMap.forEach(((s, vehicle) -> {
                if (platform.getSpots().contains(vehicle.getLocation()))
                    platform.observableListOfVehicles.add(vehicle);
            }));
        }
    }

    public static ArrayList<Platform> getPlatforms() {
        return platforms;
    }

    public static HashMap<String, Vehicle> getVehicleHashMap() {
        return vehicleHashMap;
    }

}
