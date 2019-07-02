package ftc.shift.sample.util;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Logger {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");

    public static void log(String message) {
        System.out.println(dateFormat.format(new Date()) + message);
    }
}
