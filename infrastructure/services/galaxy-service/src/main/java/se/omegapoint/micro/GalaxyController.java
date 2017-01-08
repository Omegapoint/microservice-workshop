package se.omegapoint.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GalaxyController {

    private final static Logger logger = LoggerFactory.getLogger(GalaxyController.class);
    private final GalaxyFactory galaxyFactory;

    @Autowired
    public GalaxyController(GalaxyFactory galaxyFactory) {
        this.galaxyFactory = galaxyFactory;
    }

    @RequestMapping("/galaxy")
    public Galaxy galaxy() {
        logger.info("Initializing galaxy");
        return galaxyFactory.randomGalaxy();
    }

    @RequestMapping("/ping")
    public String ping() {
        return "Pong! Galaxy Service Ready!";
    }
}
