package se.omegapoint.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class SuperHeroController {

    private final static Logger logger = LoggerFactory.getLogger(SuperHeroController.class);

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/superheroes")
    public void superPowers() {

        ResponseEntity<SuperPowersDTO> response = restTemplate.getForEntity("http://superpower-service/superpowers", SuperPowersDTO.class);
        logger.info("Response: " + response.toString());

        List<SuperPower> superPowers = response.getBody().getSuperPowers();
        superPowers.stream().forEach(power -> logger.debug(power.getPower()));

    }


}
