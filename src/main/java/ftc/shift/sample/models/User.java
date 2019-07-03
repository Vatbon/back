package ftc.shift.sample.models;

public class User {
    private String id;
    private String name;

    public User() {
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(User user1) {
        if (this == user1)
            return true;
        if (user1 == null)
            return false;
        return this.id.equals(user1.getId());
    }
}
