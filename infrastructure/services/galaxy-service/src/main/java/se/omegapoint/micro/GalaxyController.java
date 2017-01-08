package se.omegapoint.micro;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping("/galaxy/{name}")
    public Galaxy galaxy(@PathVariable("name") final String name) {
        if ("loki".equalsIgnoreCase(name)) {
            throw new IllegalArgumentException("Unknown galaxy for Loki");
        }

        logger.info("Initializing galaxy");
        return galaxyFactory.randomGalaxy();
    }

    @RequestMapping("/ping")
    public String ping() {
        return "Pong! Galaxy Service Ready!";
    }
}
