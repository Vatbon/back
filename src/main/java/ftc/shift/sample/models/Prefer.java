package ftc.shift.sample.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class Prefer {
    @ApiModelProperty(value = "Пожелание к подарку", required = true)
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