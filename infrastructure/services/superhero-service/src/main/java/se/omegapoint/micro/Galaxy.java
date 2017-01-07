package se.omegapoint.micro;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Galaxy {

    @JsonProperty
    private String name;

    @JsonCreator
    public Galaxy(String name) {
        this.name = name;
    }

    public Galaxy() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
