package se.omegapoint.micro;

import java.util.List;

public class Team {

    public final int id;
    public final String name;
    public final List<Employee> employees;

    public Team(int id, String name, List<Employee> employees) {
        this.id = id;
        this.name = name;
        this.employees = employees;
    }
}
