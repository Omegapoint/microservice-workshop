package se.omegapoint.micro;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@RestController
public class SuperPowerController {

    private final static Logger LOGGER = LoggerFactory.getLogger(SuperPowerController.class);
    private final SuperPowerFactory superPowerFactory = new SuperPowerFactory();

    @RequestMapping("/superpowers")
    public SuperPowersDTO superPowers() {
        LOGGER.info("Initializing super powers");
        List<SuperPower> randomPowers = IntStream.range(0, new Random().nextInt(5) + 1).boxed()
                .map(i -> superPowerFactory.randomSuperPower())
                .collect(toList());

        return new SuperPowersDTO(randomPowers);
    }

    @RequestMapping("/superpowers/{name}")
    public SuperPowersDTO superPowers(@PathVariable(value = "name") final String name) {
        if ("groot".equalsIgnoreCase(name)) {
            throw new IllegalArgumentException("Groot has no powers!");
        }

        LOGGER.info("Initializing super powers");
        List<SuperPower> randomPowers = IntStream.range(0, new Random().nextInt(5) + 1).boxed()
                .map(i -> superPowerFactory.randomSuperPower())
                .collect(toList());

        return new SuperPowersDTO(randomPowers);
    }

    @RequestMapping("/ping")
    public String ping() {
        return "Pong! SuperPower Service Ready!";
    }

    private class SuperPowersDTO {

        @JsonProperty("superpowers")
        private List<SuperPower> superPowers;

        @JsonCreator
        public SuperPowersDTO(@JsonProperty("superpowers") List<SuperPower> superPowers) {
            this.superPowers = superPowers;
        }

        public List<SuperPower> getSuperPowers() {
            return superPowers;
        }

        public void setSuperPowers(List<SuperPower> superPowers) {
            this.superPowers = superPowers;
        }
    }
}
