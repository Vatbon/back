package ftc.shift.secretsanta.models;

public class ParticipantQueryEntity extends Participant {

    private String groupId;

    public ParticipantQueryEntity(User user, String prefer, boolean received, boolean presented, String groupId) {
        super(user, prefer, received, presented);
        this.groupId = groupId;
    }

    public ParticipantQueryEntity() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
