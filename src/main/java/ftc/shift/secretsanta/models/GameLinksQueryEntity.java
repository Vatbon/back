package ftc.shift.secretsanta.models;

public class GameLinksQueryEntity {
    private String userIdFrom;
    private String userIdTo;

    public GameLinksQueryEntity() {
    }

    public String getUserIdFrom() {
        return userIdFrom;
    }

    public void setUserIdFrom(String userIdFrom) {
        this.userIdFrom = userIdFrom;
    }

    public String getUserIdTo() {
        return userIdTo;
    }

    public void setUserIdTo(String userIdTo) {
        this.userIdTo = userIdTo;
    }
}
