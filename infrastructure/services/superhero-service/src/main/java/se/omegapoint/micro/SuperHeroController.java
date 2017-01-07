package se.omegapoint.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
        logger.info("Initializing super heo");
        return superHeroFactory.randomSuperHero();
    }


}
