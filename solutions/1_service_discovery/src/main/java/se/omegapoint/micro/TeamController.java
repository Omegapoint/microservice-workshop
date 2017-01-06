package se.omegapoint.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TeamController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamController.class);

    public final List<String> teamMembers = new ArrayList<>();

    public TeamController() {
        teamMembers.add("Daniel Hultqvist");
        teamMembers.add("Pontus Thulin");
    }

    @RequestMapping("members")
    public List<String> members() {
        LOGGER.info("Retrieving team members!");
        return teamMembers;
    }

    @RequestMapping(value = "members", method = RequestMethod.POST)
    public String addTeamMember(final @RequestBody AddMemberRequest addMemberRequest) {
        if (addMemberRequest.getName() == null  || addMemberRequest.getName().length() == 0) {
            LOGGER.warn("Invalid request to add members");
            return "Name cannot be null or empty";
        }

        teamMembers.add(addMemberRequest.getName());

        LOGGER.info("Added team member " + addMemberRequest.getName());
        return "Added team member " + addMemberRequest.getName();
    }
}
