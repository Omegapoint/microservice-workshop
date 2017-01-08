package se.omegapoint.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SuperHeroController {

    private final static Logger logger = LoggerFactory.getLogger(SuperHeroController.class);
    private final SuperHeroFactory superHeroFactory;

    @Autowired
    public SuperHeroController(SuperHeroFactory superHeroFactory) {
        this.superHeroFactory = superHeroFactory;
    }

    @RequestMapping("/superhero")
    public SuperHero superHero() {
        logger.info("Initializing super hero");

        return superHeroFactory.randomSuperHero();
    }

    @RequestMapping("/superhero/{name}")
    public SuperHero superHero(@PathVariable(value = "name") final String name) {
        logger.info("Initializing super hero");
        return superHeroFactory.specificSuperHero(name);
    }

    @RequestMapping("/ping")
    public String ping() {
        return "Pong! SuperHero Service Ready!";
    }
}
