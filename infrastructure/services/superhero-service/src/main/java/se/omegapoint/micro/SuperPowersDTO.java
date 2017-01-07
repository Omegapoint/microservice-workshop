package se.omegapoint.micro;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class SuperPowersDTO {

    @JsonProperty("superpowers")
    private List<SuperPower> superPowers;

    @JsonCreator
    public SuperPowersDTO(@JsonProperty("superpowers") List<SuperPower> superPowers) {
        this.superPowers = superPowers;
    }

    public List<SuperPower> getSuperPowers() {
        return superPowers;
    }

    public void setSuperPowers(List<SuperPower> superPowers) {
        this.superPowers = superPowers;
    }
}