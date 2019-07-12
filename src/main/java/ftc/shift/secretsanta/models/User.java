package ftc.shift.secretsanta.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "Модель пользователя")
public class User {
    @ApiModelProperty(value = "Уникальный идентификатор пользователя", required = false)
    private String id;
    @ApiModelProperty(value = "Имя пользователя от 2 до 30 символов без специальных символов вроде !, \" ,№ ,; ,% ,: ,? ,*", required = true)
    private String name;
    @ApiModelProperty(value = "Список групп, в которых польщователь принмает участие", required = false)
    private final List<String> groupsAsParticipant = new ArrayList<>();
    @ApiModelProperty(value = "Список групп, где пользователь является создателем", required = false)
    private final List<String> groupsAsHost = new ArrayList<>();

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

    public void addGroupAsParticipant(String groupId) {
        if (!groupsAsParticipant.contains(groupId))
            groupsAsParticipant.add(groupId);
    }

    public void addGroupAsHost(String groupId) {
        if (!groupsAsHost.contains(groupId))
            groupsAsHost.add(groupId);
    }

    public void deleteGroupAsParticipant(String groupId) {
        groupsAsParticipant.remove(groupId);
    }

    public void deleteGroupAsHost(String groupId) {
        groupsAsHost.remove(groupId);
    }

    public List<String> getGroupsAsParticipant() {
        return groupsAsParticipant;
    }

    public List<String> getGroupsAsHost() {
        return groupsAsHost;
    }
}
