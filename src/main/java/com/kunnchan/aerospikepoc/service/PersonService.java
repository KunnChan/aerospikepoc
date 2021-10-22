package com.kunnchan.aerospikepoc.service;
/*
 * Created by kunnchan on 22/10/2021
 * package :  com.kunnchan.aerospikepoc.service
 */

import com.kunnchan.aerospikepoc.ducuments.Person;
import com.kunnchan.aerospikepoc.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;

    public Person getById(String id){
        return personRepository.findById(id).orElse(null);
    }

    public Person create(Person person){
        return personRepository.save(person);
    }

    public Iterable<Person> findAll(){
        return personRepository.findAll();
    }

    public Iterable<Person> findAllByIds(Iterable<String> ids){
        return personRepository.findAllById(ids);
    }

    public void delete(String id){
        personRepository.deleteById(id);
        log.info("Successfully Deleted for id {}", id);
    }

    public List<Person> findByFirstName(String firstName){
        return personRepository.findByFirstName(firstName);
    }
}
