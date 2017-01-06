package se.omegapoint.micro;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompetendeDayController {

    public CompetendeDayController() {
    }

    @RequestMapping("/competencedays")
    public String teams() {
        return "Pong";
    }
}
