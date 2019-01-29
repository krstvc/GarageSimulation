package garage.utility.logging;

import java.util.Date;
import java.util.logging.LogRecord;

public class Formatter extends java.util.logging.Formatter {

    @Override
    public String format(LogRecord record) {
        return record.getThreadID() + "::"
                + record.getSourceClassName() + "::"
                + record.getSourceMethodName() + "::"
                + new Date(record.getMillis()) + "::"
                + record.getMessage() + System.getProperty("line.separator");
    }

}
