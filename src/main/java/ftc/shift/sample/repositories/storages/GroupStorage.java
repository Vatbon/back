package ftc.shift.sample.repositories.storages;

import ftc.shift.sample.models.Group;

import java.util.HashMap;
import java.util.Map;

public class GroupStorage {
    private Map<String, Group> groupCache = new HashMap<>();

    public Map<String, Group> getCache(){
        //Read from memory
        return groupCache;
    }
}
