package se.omegapoint.micro;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EmployeeController {

    private final List<Employee> employees;

    public EmployeeController() {
        this.employees = new ArrayList<>();
        this.employees.add(new Employee(1, "Pontus Thulin"));
        this.employees.add(new Employee(2, "Daniel Hultqvist"));
    }

    @RequestMapping("/employees")
    public List<Employee> employees() {
        return this.employees;
    }
}
