package ftc.shift.secretsanta.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");

    public static void log(String message) {
        System.out.println(dateFormat.format(new Date()) + message);
    }
}
