package garage;

import garage.utility.GarageProperties;
import garage.utility.logging.GarageLogger;
import garage.vehicles.Vehicle;
import garage.vehicles.police.Police;
import javafx.collections.FXCollections;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;


public class Garage {

    private static File SERIALIZATION_FILE = new File(System.getProperty("user.home") + File.separatorChar
            + "Documents" + File.separatorChar + "garage files" + File.separatorChar + "garage.ser");
    private static File PAYMENT_EVIDENCE_FILE = new File(System.getProperty("user.home") + File.separatorChar
            + "Documents" + File.separatorChar + "garage files" + File.separatorChar + "payment.csv");

    private static ArrayList<Platform> platforms;
    private static HashMap<String, Vehicle> vehicleHashMap;
    private static HashMap<String, LocalDateTime> paymentEvidenceHashMap;


    public static void setupGarage() throws Exception {
        //Setting up required files first
        GarageLogger.setupLogger();
        GarageProperties.setupProperties();

        if (SERIALIZATION_FILE.exists()) {
            try {
                readFromSerializationFile();                //Try to read from serialization file if possible
            } catch (ClassNotFoundException exception) {
                setupNewGarage();                           //Create new garage if the file is unable to read from file
            }
        } else
            setupNewGarage();                               //Create new garage if there is no serialization file

        if (!PAYMENT_EVIDENCE_FILE.exists()) {
            PAYMENT_EVIDENCE_FILE.getParentFile().mkdirs();
            PAYMENT_EVIDENCE_FILE.createNewFile();
            try (PrintWriter writer = new PrintWriter(new FileOutputStream(PAYMENT_EVIDENCE_FILE))) {
                writer.write("registration_number,hours_spent_in_garage,amount_to_pay" + System.getProperty("line.separator"));
            } catch (IOException exception) {
                GarageLogger.log(Level.WARNING, "unable to write to .csv file", exception);
            }
        }
        if (!Police.WANTED_VEHICLES_FILE.exists()) {
            Police.WANTED_VEHICLES_FILE.getParentFile().mkdirs();
            Police.WANTED_VEHICLES_FILE.createNewFile();
        }

        setObservableLists();
    }

    private static void readFromSerializationFile() throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(SERIALIZATION_FILE));
        platforms = (ArrayList<Platform>) stream.readObject();
        vehicleHashMap = (HashMap<String, Vehicle>) stream.readObject();
        paymentEvidenceHashMap = (HashMap<String, LocalDateTime>) stream.readObject();
        stream.close();
    }

    public static void writeToSerializationFile() throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FILE));
        stream.writeObject(platforms);
        stream.writeObject(vehicleHashMap);
        stream.writeObject(paymentEvidenceHashMap);
        stream.close();
    }

    private static void setupNewGarage() {
        platforms = new ArrayList<>();
        for (int i = 0; i < GarageProperties.getNumberOfPlatformsValue(); ++i)
            platforms.add(new Platform());
        vehicleHashMap = new HashMap<>();
        paymentEvidenceHashMap = new HashMap<>();
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

    public static void chargeOnExit(Vehicle vehicle) {
        try (FileWriter writer = new FileWriter(PAYMENT_EVIDENCE_FILE, true)) {
            LocalDateTime entered = paymentEvidenceHashMap.get(vehicle.getRegistrationNumber());
            LocalDateTime left = LocalDateTime.now();

            long hours = ChronoUnit.HOURS.between(entered, left);
            int amountToPay;
            if (hours == 0)
                amountToPay = 1;
            else if (hours < 3)
                amountToPay = 2;
            else
                amountToPay = 8;

            writer.append(vehicle.getRegistrationNumber() + "," + hours + "," + amountToPay + System.getProperty("line.separator"));
            writer.flush();
        } catch (Exception exception) {
            GarageLogger.log(Level.WARNING, "unable to write to .csv file", exception);
        }
    }

    public static ArrayList<Platform> getPlatforms() {
        return platforms;
    }

    public static HashMap<String, Vehicle> getVehicleHashMap() {
        return vehicleHashMap;
    }

    public static HashMap<String, LocalDateTime> getPaymentEvidenceHashMap() {
        return paymentEvidenceHashMap;
    }

}
