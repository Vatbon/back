package ftc.shift.sample.models;

import ftc.shift.sample.util.Logger;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Collection;

@ApiModel
public class Group implements Cloneable {
    @ApiModelProperty(value = "Уникальный идентификатор события", required = false)
    private String id;

    @ApiModelProperty(value = "Названия группы", required = true)
    private String title;

    @ApiModelProperty(value = "Дата и время начала", required = true)
    private String startTime;

    @ApiModelProperty(value = "Дата и время окончания", required = true)
    private String endTime;

    @ApiModelProperty(value = "Идентификатор начала события", required = false)
    private boolean started;

    @ApiModelProperty(value = "Идентификатор окончания события", required = false)
    private boolean finished;

    @ApiModelProperty(value = "Ограничение на количество участников", required = true)
    private int amountLimit;

    @ApiModelProperty(value = "Количество участников на данный момент", required = false)
    private int amount;

    @ApiModelProperty(value = "Минимальная сумма подарка", required = true)
    private int minValue;

    @ApiModelProperty(value = "Максимальная сумма подарка", required = true)
    private int maxValue;

    @ApiModelProperty(value = "Способ дарения подарка", required = true)
    private String method;

    @ApiModelProperty(value = "Создатель группы", required = false)
    private User host;

    @ApiModelProperty(value = "Лист участников", required = false)
    private Collection<Participant> participants = new ArrayList<>();

    public Group() {
    }

    public Group(String id, String title, String startTime, String endTime, int amount, int amountLimit,
                 int minValue, int maxValue, String method, User host, boolean started, boolean finished) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.amountLimit = amountLimit;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.method = method;
        this.host = host;
        this.started = started;
        this.finished = finished;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
        Collection<User> result = new ArrayList<>();
        for (Participant participant : participants) {
            result.add(participant.user);
        }
        return result;
    }

    public void addParticipant(User user, String prefer) {
        for (Participant participant : participants) {
            if (participant.user.equals(user))
                return;
        }
        this.participants.add(new Participant(user, prefer, false));
        amount++;
    }

    public void deleteParticipant(User user) {
        for (Participant participant : participants) {
            if (participant.user.equals(user)) {
                participants.remove(participant);
                amount--;
            }
        }
    }

    public void receiveGift(User user) {
        for (Participant participant : participants) {
            if (participant.user.equals(user)) {
                participant.received = true;
                return;
            }
        }
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

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPrefer(String userId) {
        for (Participant participant : participants) {
            if (participant.user.getId().equals(userId))
                return participant.prefer;
        }
        return null;
    }

    public boolean isReceived(String userId) {
        for (Participant participant : participants) {
            if (participant.user.getId().equals(userId))
                return participant.received;
        }
        return false;
    }

    private class Participant {
        User user;
        String prefer;
        boolean received;

        Participant(User user, String prefer, boolean received) {
            this.user = user;
            this.prefer = prefer;
            this.received = received;
        }
    }

    @Override
    public Group clone() {
        Logger.log("groupId = " + this.id + " has " + getAmount() + " participants");
        Group clone = new Group(this.id, this.title, this.startTime, this.endTime, getAmount(), this.amountLimit,
                this.minValue, this.maxValue, this.method, this.host, this.started, this.finished);
        //implement cloning participants?
        return clone;
    }
}