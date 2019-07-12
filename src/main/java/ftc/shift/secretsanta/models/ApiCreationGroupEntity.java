package ftc.shift.secretsanta.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Модель для осуществления запроса POST /api/v2/groups")
public class ApiCreationGroupEntity {
    @ApiModelProperty(value = "Тело группы", required = true)
    private Group group;
    @ApiModelProperty(value = "Тело \"желания\"", required = true)
    private Prefer prefer;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Prefer getPrefer() {
        return prefer;
    }

    public void setPrefer(Prefer prefer) {
        this.prefer = prefer;
    }
}
