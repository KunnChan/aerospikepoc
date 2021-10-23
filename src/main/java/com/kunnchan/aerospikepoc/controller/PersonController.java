package com.kunnchan.aerospikepoc.controller;
/*
 * Created by kunnchan on 22/10/2021
 * package :  com.kunnchan.aerospikepoc.controller
 */

import com.kunnchan.aerospikepoc.ducuments.Person;
import com.kunnchan.aerospikepoc.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public Person create(@RequestBody Person person){
       return personService.create(person);
    }

    @GetMapping
    public Iterable<Person> getAll(){
        return personService.findAll();
    }

    @GetMapping("/ids")
    public Iterable<Person> getAllByIds(@RequestParam List<String> ids){
        log.info("GetAllByIds {} ", ids);
        return personService.findAllByIds(ids);
    }

    @GetMapping("/{id}")
    public Person getById(@PathVariable String id){
        return personService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id){
        personService.delete(id);
    }

    @GetMapping("/name/{firstName}")
    public List<Person> getByFirstName(@PathVariable(value = "firstName") String firstName){
        log.info("getByFirstName param : {}", firstName);
        return personService.findByFirstName(firstName);
    }
}
