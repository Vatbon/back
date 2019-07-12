package ftc.shift.secretsanta.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Модель \"желания\"")
public class Prefer {
    @ApiModelProperty(value = "Пожелание к подарку не более 200 символов", required = true)
    private String prefer;

    public Prefer() {
    }

    public Prefer(String prefer) {
        this.prefer = prefer;
    }

    public String getPrefer() {
        return prefer;
    }

    public void setPrefer(String prefer) {
        this.prefer = prefer;
    }
}