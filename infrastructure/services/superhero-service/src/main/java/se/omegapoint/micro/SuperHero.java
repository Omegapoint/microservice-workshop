package se.omegapoint.micro;


import java.util.List;

public class SuperHero {

    public final String name;
    public final Galaxy galaxy;
    public final List<SuperPower> powers;

    public SuperHero(String name, Galaxy galaxy, List<SuperPower> powers) {
        this.name = name;
        this.galaxy = galaxy;
        this.powers = powers;
    }
}
