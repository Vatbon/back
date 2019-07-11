package ftc.shift.secretsanta.util;

public class IdFactory {
    private static long id = 0;

    public static long getNewId() {
        id++;
        return id;
    }
}