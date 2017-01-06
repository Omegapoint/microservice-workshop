package se.omegapoint.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

@RestController
public class TeamController {

    private final static Logger LOGGER = LoggerFactory.getLogger(TeamController.class);
    private final List<Team> teams;

    @Autowired
    RestTemplate restTemplate;

    public TeamController() {
        this.teams = new ArrayList<>();
        this.teams.add(new Team(1, "Appsäk Nord", asList(new Employee(1, "Pontus Thulin"))));
    }

    @RequestMapping("/teams")
    public List<Team> teams() {

        LOGGER.info("Fetching teams");

        ResponseEntity<Employee[]> response = restTemplate.getForEntity("http://employee-service/employees", Employee[].class);
        List<Employee> employees = Arrays.asList(response.getBody());
        teams.add(new Team(teams.size() + 1, "ASDF", employees ));
        return this.teams;
    }
}
