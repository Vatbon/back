package ftc.shift.sample.models;

import java.util.HashMap;
import java.util.Map;

/**
 * links information:
 * Key is user giving a present
 * Value is user receiving the present
 */
public class GameInfo {

    private Group group;

    private Map<User, User> links = new HashMap<>();

    public GameInfo() {
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Map<User, User> getLinks() {
        return links;
    }

    public void setLinks(Map<User, User> links) {
        this.links = links;
    }
}
