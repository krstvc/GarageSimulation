package garage.utility.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GarageLogger {

    private static File LOG_FILE = new File(System.getProperty("user.home") + File.separatorChar
            + "Documents" + File.separatorChar + "garage files" + File.separatorChar + "logging" + File.separatorChar
            + "error.log");

    private static Logger logger;

    public static void setupLogger() throws IOException {
        LOG_FILE.getParentFile().mkdirs();

        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        Handler handler = new FileHandler(LOG_FILE.toString(), true);
        handler.setFormatter(new Formatter());
        logger.addHandler(handler);
    }

    public static void log(Level level, String message, Throwable thrown) {
        logger.log(level, message, thrown);
    }

}
