package ftc.shift.sample.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class User {
    @ApiModelProperty(value = "Уникальный идентификатор пользователя", required = false)
    private String id;
    @ApiModelProperty(value = "Имя пользователя", required = true)
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
