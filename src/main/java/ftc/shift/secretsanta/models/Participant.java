package ftc.shift.secretsanta.models;

public class Participant {
    private User user;
    private String prefer;
    private boolean received;
    private boolean presented;

    public Participant(User user, String prefer, boolean received, boolean presented) {
        this.user = user;
        this.prefer = prefer;
        this.received = received;
        this.presented = presented;
    }

    public Participant() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPrefer() {
        return prefer;
    }

    public void setPrefer(String prefer) {
        this.prefer = prefer;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public boolean isPresented() {
        return presented;
    }

    public void setPresented(boolean presented) {
        this.presented = presented;
    }
}
