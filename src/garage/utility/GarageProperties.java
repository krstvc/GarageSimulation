package garage.utility;

import garage.utility.logging.GarageLogger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

public class GarageProperties {

    private static File PROPERTIES_FILE = new File(System.getProperty("user.home") + File.separatorChar
            + "Documents" + File.separatorChar + "garage files" + File.separatorChar + "garage.properties");

    private static String numberOfPlatformsKey = "NUMBER_OF_PLATFORMS";
    private static int numberOfPlatformsValue;
    private static int defaultNumberOfPlatforms = 5;

    private static Properties properties = new Properties();

    public static void setupProperties() throws IOException {
        if (PROPERTIES_FILE.exists()) {
            FileReader reader = new FileReader(PROPERTIES_FILE);
            properties.load(reader);
            reader.close();

            try {
                numberOfPlatformsValue = Integer.parseInt(properties.getProperty(numberOfPlatformsKey));
                if (numberOfPlatformsValue < 1)
                    throw new NumberFormatException();
            } catch (Exception exception) {
                GarageLogger.log(Level.WARNING, "invalid value found in properties file, set to default value", exception);
            }

        } else {
            PROPERTIES_FILE.getParentFile().mkdirs();
            PROPERTIES_FILE.createNewFile();
            numberOfPlatformsValue = defaultNumberOfPlatforms;
            properties.setProperty(numberOfPlatformsKey, numberOfPlatformsValue + "");
        }

        FileWriter writer = new FileWriter(PROPERTIES_FILE);
        properties.store(writer, "");
        writer.close();
    }

    public static int getNumberOfPlatformsValue() {
        return numberOfPlatformsValue;
    }

    public static Properties getProperties() {
        return properties;
    }
}
