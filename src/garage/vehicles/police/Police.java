package garage.vehicles.police;

import garage.utility.logging.GarageLogger;
import garage.vehicles.Rotation;
import garage.vehicles.Vehicle;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;

public interface Police extends Rotation {

    int PRIORITY = 1;

    File WANTED_VEHICLES_FILE = new File(System.getProperty("user.home") + File.separatorChar
            + "Documents" + File.separatorChar + "garage files" + File.separatorChar + "wanted.txt");
    File CRASH_RECORD_FILE = new File(System.getProperty("user.home") + File.separatorChar
            + "Documents" + File.separatorChar + "garage files" + File.separatorChar
            + "records" + File.separatorChar + "crash_record");
    File WANTED_RECORD_FILE = new File(System.getProperty("user.home") + File.separatorChar
            + "Documents" + File.separatorChar + "garage files" + File.separatorChar
            + "records" + File.separatorChar + "wanted_record");

    static void writeCrashDetails(Vehicle actor1, Vehicle actor2, Vehicle police, Vehicle sanitary, Vehicle firefighting) {
        LocalDateTime crashTime = LocalDateTime.now();
        File currentCrashFile = new File(CRASH_RECORD_FILE.toString() + "_" + crashTime.getDayOfWeek() + "_" + crashTime.getHour() + "_" + crashTime.getMinute() + "_" + crashTime.getSecond());
        currentCrashFile.getParentFile().mkdirs();
        System.out.println("Writing to: " + currentCrashFile);
        try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(currentCrashFile))) {
            stream.writeChars(actor1.getName() + ":" + actor1.getRegistrationNumber() + ":" + actor1.getImageURI() + "|");
            stream.writeChars(actor2.getName() + ":" + actor2.getRegistrationNumber() + ":" + actor2.getImageURI() + "|");
            stream.writeChars(police.getName() + ":" + police.getRegistrationNumber() + ":" + police.getImageURI() + "|");
            stream.writeChars(sanitary.getName() + ":" + sanitary.getRegistrationNumber() + ":" + sanitary.getImageURI() + "|");
            stream.writeChars(firefighting.getName() + ":" + sanitary.getRegistrationNumber() + ":" + firefighting.getImageURI());
        } catch (IOException exception) {
            GarageLogger.log(Level.WARNING, "unable to write to crash record file", exception);
        }
    }

    static void writeWantedVehicleDetails(Vehicle wanted, Vehicle police) {
        LocalDateTime catchTime = LocalDateTime.now();
        File currentCatchFile = new File(WANTED_RECORD_FILE.toString() + "_" + catchTime.getDayOfWeek() + "_" + catchTime.getHour() + "_" + catchTime.getMinute() + "_" + catchTime.getSecond());
        currentCatchFile.getParentFile().mkdirs();
        try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(currentCatchFile))) {
            stream.writeChars(wanted.getName() + ":" + wanted.getRegistrationNumber() + ":" + wanted.getImageURI() + "|");
            stream.writeChars(police.getName() + ":" + police.getRegistrationNumber() + ":" + police.getImageURI());
        } catch (IOException exception) {
            GarageLogger.log(Level.WARNING, "unable to write to wanted vehicle record file", exception);
        }
    }

}
