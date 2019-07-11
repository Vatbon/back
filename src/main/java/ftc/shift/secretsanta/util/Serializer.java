package ftc.shift.secretsanta.util;

import java.io.*;

public class Serializer {

    public static final int USER_CACHE = 1;
    public static final int GROUP_CACHE = 2;

    private static final String usersFilePath = "./data/users";
    private static final String groupsFilePath = "./data/groups";

    private static File usersFile = null;
    private static File groupsFile = null;

    public static void saveObject(int status, Object object) {

        if (status == USER_CACHE) {
            if (usersFile == null) {
                usersFile = new File(usersFilePath);
            }
            try {
                ObjectOutputStream fw = new ObjectOutputStream(new FileOutputStream(usersFile));
                fw.writeObject(object);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (status == GROUP_CACHE) {
            if (groupsFile == null) {
                groupsFile = new File(groupsFilePath);
            }
            try {
                ObjectOutputStream fw = new ObjectOutputStream(new FileOutputStream(groupsFile));
                fw.writeObject(object);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object readObject(int status){
        return null;
    }

}
