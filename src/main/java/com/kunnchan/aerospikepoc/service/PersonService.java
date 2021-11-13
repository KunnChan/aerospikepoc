package com.kunnchan.aerospikepoc.service;
/*
 * Created by kunnchan on 22/10/2021
 * package :  com.kunnchan.aerospikepoc.service
 */

import com.aerospike.client.*;
import com.aerospike.client.exp.Exp;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.kunnchan.aerospikepoc.config.AerospikeConfig;
import com.kunnchan.aerospikepoc.ducuments.Person;
import com.kunnchan.aerospikepoc.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.query.Qualifier;
import org.springframework.data.aerospike.repository.query.Criteria;
import org.springframework.data.aerospike.repository.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final AerospikeClient aerospikeClient;
    private final AerospikeTemplate aerospikeTemplate;
    private final AerospikeConfig.AerospikeConfigurationProperties aerospikeProperties;

    // TODO: query 1M records, aggregate function count, max, top N records, dateRange, pagination

    public Person getById(String id){
        return personRepository.findById(id).orElse(null);
    }

    public Person create(Person person){
        return personRepository.save(person);
    }

    public List<Person> findAll(){
        //List<Person> persons = new ArrayList<>();
        //personRepository.findAll().forEach(persons::add);
        //return persons;
        return queryByPolicy(getScanPolicy());
    }

    public Iterable<Person> findAllByIds(Iterable<String> ids){
        return personRepository.findAllById(ids);
    }

    public void delete(String id){
        personRepository.deleteById(id);
        log.info("Successfully Deleted for id {}", id);
    }

    public List<Person> findByFirstName(String firstName){
        ScanPolicy policy = getScanPolicy();
        policy.filterExp = Exp.build(Exp.eq(Exp.stringBin("firstName"), Exp.val(firstName)));
        return queryByPolicy(policy);
    }

    private ScanPolicy getScanPolicy(){
        ScanPolicy policy = new ScanPolicy();
        policy.concurrentNodes = true;
        policy.includeBinData = true;
        return policy;
    }

    public List<Person> queryByPolicy(ScanPolicy policy){
        List<Person> persons = Collections.synchronizedList(new ArrayList<>());
        aerospikeClient.scanAll(policy, aerospikeProperties.getNamespace(), aerospikeProperties.getSetName(), (key, record) -> {
            if(record != null){
                String id = key.userKey.toString();
                String fName = (String) record.getValue("firstName");
                String lastName = (String) record.getValue("lastName");
                persons.add(new Person(id, fName, lastName, ""));
            }
        });
        return persons;
    }

    // statement query required Index to be created first
    private void statementExample(String param){
        RecordSet rs = null;
        Statement stmt = new Statement();
        stmt.setNamespace("playground");
        stmt.setSetName("person");
        stmt.setFilter(Filter.equal("firstName", param));
        rs = aerospikeClient.query(null, stmt);
        while (rs.next()) {
            Record r = rs.getRecord();
            log.info(r.getValue("firstName").toString() + "\n");
        }
    }

    public List<Person> getPersonsByCriteria(String firstName){
        Query query = new Query(Criteria.where("firstName").is(firstName, "firstName"));
        Set<String> ids = new HashSet<>();
        ids.add(firstName);
        return aerospikeTemplate.findByIds(ids, Person.class);
    }

    public List<Person> getPersonsByName(String firstName, String lastName) {

//        Criteria criteria1 = new Criteria();
        Set<String> ids = new HashSet<>();
        ids.add("johndoe");
        ids.add("willsmith");
        ids.add("willson");
//        criteria1.is(ids, "firstName");
//
//        Criteria criteria2 = new Criteria();
//        criteria2.is(lastName, "lastName");
//
//        criteria1.getCriteriaChain().add(criteria2);

        List<Predicate<Person>> predicates = new ArrayList<>();
        if(StringUtils.hasLength(firstName)){
            predicates.add(str -> str.getFirstName().equalsIgnoreCase(firstName));
        }
        if(StringUtils.hasLength(lastName)){
            predicates.add(str -> str.getLastName().equalsIgnoreCase(lastName));
        }
        return StreamSupport.stream(personRepository.findAllById(ids).spliterator(), false)
                .filter(predicates.stream().reduce(x->true, Predicate::and))
                .collect(Collectors.toList());
//        return aerospikeTemplate.findByIds(ids, Person.class).stream()
//                .filter(predicates.stream().reduce(x->true, Predicate::and))
//                .collect(Collectors.toList());
    }

    public Person create(Integer noOfRecord, Person person) {
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < noOfRecord; i++) {
            persons.add(new Person(person.getId() + i, person.getFirstName() + i, person.getLastName() + i, person.getAddress()));
        }
        personRepository.saveAll(persons);
        return persons.get(persons.size() -1);
    }
}
