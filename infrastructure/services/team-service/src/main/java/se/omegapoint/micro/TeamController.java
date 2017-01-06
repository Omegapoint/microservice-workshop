package se.omegapoint.micro;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

@RestController
public class TeamController {

    private final List<Team> teams;

    public TeamController() {
        this.teams = new ArrayList<>();
        this.teams.add(new Team(1, "Appsäk Nord", asList(new Employee(1, "Pontus Thulin"))));
    }

    @RequestMapping("/teams")
    public List<Team> teams() {
        return this.teams;
    }
}
