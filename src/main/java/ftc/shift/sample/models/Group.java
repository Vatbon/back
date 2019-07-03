package ftc.shift.sample.models;

import java.util.ArrayList;
import java.util.Collection;

public class Group {
    private String id;
    private String name;
    private String startTime;
    private String deadLine;
    private boolean started;
    private boolean finished;
    private int amountLimit;
    private int amount;
    private User host;
    private Collection<User> participants = new ArrayList<>();

    public Group() {
    }

    public Group(String id, String name, String startTime, String deadLine, int amountLimit, User host) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.deadLine = deadLine;
        this.amountLimit = amountLimit;
        this.host = host;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(String deadLine) {
        this.deadLine = deadLine;
    }

    public int getAmountLimit() {
        return amountLimit;
    }

    public void setAmountLimit(int amountLimit) {
        this.amountLimit = amountLimit;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Collection<User> getAllParticipants() {
        return this.participants;
    }

    public void addParticipant(User user) {
        if (!this.participants.contains(user))
            this.participants.add(user);
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getAmount() {
        amount = participants.size();
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean equals(Group group1) {
        if (this == group1)
            return true;
        if (group1 == null)
            return false;
        return this.id.equals(group1.getId());
    }
}
