package se.omegapoint.micro;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SuperPower {

    @JsonProperty
    private String power;

    public SuperPower() {
    }

    @JsonCreator
    public SuperPower(@JsonProperty String power) {
        this.power = power;
    }


    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }
}
