package ftc.shift.sample.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ResponsePreferEntity {
    @ApiModelProperty(value = "Пользователь, которому нужно сделать подарок", required = true)
    private User user;
    @ApiModelProperty(value = "Пожелание к подарку данного пользователя", required = true)
    private String prefer;
    @ApiModelProperty(value = "Информация о том, получил ли данный пользователь подарок", required = true)
    private boolean received;

    ResponsePreferEntity() {
    }

    ResponsePreferEntity(User user, String prefer, boolean received) {
        this.user = user;
        this.prefer = prefer;
        this.received = received;
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
}
