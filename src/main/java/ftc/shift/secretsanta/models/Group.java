package ftc.shift.secretsanta.models;

import ftc.shift.secretsanta.util.Logger;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@ApiModel
public class Group implements Cloneable {
    @ApiModelProperty(value = "Уникальный идентификатор события", required = false)
    private String id;

    @ApiModelProperty(value = "Названия группы от 3 до 50 символов", required = true)
    private String title;

    @ApiModelProperty(value = "Дата и время начала формата дд.мм.гггг", required = true)
    private String startTime;

    @ApiModelProperty(value = "Дата и время окончания формата дд.мм.гггг", required = true)
    private String endTime;

    @ApiModelProperty(value = "Идентификатор начала события", required = false)
    private boolean started;

    @ApiModelProperty(value = "Идентификатор окончания события", required = false)
    private boolean finished;

    @ApiModelProperty(value = "Ограничение на количество участников. 0 означает неограниченное кол-во участников", required = true)
    private int amountLimit;

    @ApiModelProperty(value = "Количество участников на данный момент", required = false)
    private int amount;

    @ApiModelProperty(value = "Минимальная сумма подарка. От 0 до maxValue", required = true)
    private int minValue;

    @ApiModelProperty(value = "Максимальная сумма подарка. От minValue до 5000", required = true)
    private int maxValue;

    @ApiModelProperty(value = "Способ дарения подарка до 200 символов", required = true)
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
        synchronized (participants) {
            for (Participant participant : participants) {
                result.add(participant.user);
            }
        }
        return result;
    }

    public void addParticipant(User user, String prefer) {
        synchronized (participants) {
            for (Participant participant : participants) {
                if (participant.user.equals(user))
                    return;
            }
            this.participants.add(new Participant(user, prefer, false, false));
        }
        amount++;
    }

    public void deleteParticipant(User user) {
        synchronized (participants) {
            Iterator<Participant> iterator = participants.iterator();
            while (iterator.hasNext()) {
                Participant participant = iterator.next();
                if (participant.user.equals(user)) {
                    iterator.remove();
                    amount--;
                }
            }
        }
    }

    public void receiveGift(User user) {
        synchronized (participants) {
            Iterator<Participant> iterator = participants.iterator();
            while (iterator.hasNext()) {
                Participant participant = iterator.next();
                if (participant.user.equals(user)) {
                    participant.received = true;
                    return;
                }
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
        synchronized (participants) {
            Iterator<Participant> iterator = participants.iterator();
            while (iterator.hasNext()) {
                Participant participant = iterator.next();
                if (participant.user.getId().equals(userId))
                    return participant.prefer;
            }
        }
        return null;
    }

    public boolean isReceived(String userId) {
        synchronized (participants) {
            Iterator<Participant> iterator = participants.iterator();
            while (iterator.hasNext()) {
                Participant participant = iterator.next();
                if (participant.user.getId().equals(userId))
                    return participant.received;
            }
        }
        return false;
    }

    public void presentGift(User user) {
        synchronized (participants) {
            Iterator<Participant> iterator = participants.iterator();
            while (iterator.hasNext()) {
                Participant participant = iterator.next();
                if (participant.user.equals(user)) {
                    participant.presented = true;
                    return;
                }
            }
        }
    }

    private class Participant {
        User user;
        String prefer;
        boolean received;
        boolean presented;

        Participant(User user, String prefer, boolean received, boolean presented) {
            this.user = user;
            this.prefer = prefer;
            this.received = received;
            this.presented = presented;
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