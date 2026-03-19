package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;
    private Taxable taxable;

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        this.developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAll() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer create(@RequestBody Developer developer) {
        Developer savedDeveloper = null;
        double taxRate = 0;
        double netSalary = 0;

        if (developer.getExperience() == Experience.JUNIOR) {
            taxRate = taxable.getSimpleTaxRate();
            netSalary = developer.getSalary() - (developer.getSalary() * taxRate / 100);
            savedDeveloper = new JuniorDeveloper(developer.getId(), developer.getName(), netSalary);
        }
        else if (developer.getExperience() == Experience.MID) {
            taxRate = taxable.getMiddleTaxRate();
            netSalary = developer.getSalary() - (developer.getSalary() * taxRate / 100);
            savedDeveloper = new MidDeveloper(developer.getId(), developer.getName(), netSalary);
        }
        else if (developer.getExperience() == Experience.SENIOR) {
            taxRate = taxable.getUpperTaxRate();
            netSalary = developer.getSalary() - (developer.getSalary() * taxRate / 100);
            savedDeveloper = new SeniorDeveloper(developer.getId(), developer.getName(), netSalary);
        }

        if (savedDeveloper != null) {
            developers.put(savedDeveloper.getId(), savedDeveloper);
        }

        return savedDeveloper;
    }

    @PutMapping("/{id}")
    public Developer update(@PathVariable int id, @RequestBody Developer developer) {
        developer.setId(id);
        developers.put(id, developer);
        return developer;
    }

    @DeleteMapping("/{id}")
    public Developer delete(@PathVariable int id) {
        return developers.remove(id);
    }
}