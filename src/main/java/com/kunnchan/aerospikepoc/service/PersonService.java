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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final AerospikeClient aerospikeClient;
    private final AerospikeConfig.AerospikeConfigurationProperties aerospikeProperties;

    public Person getById(String id){
        return personRepository.findById(id).orElse(null);
    }

    public Person create(Person person){
        return personRepository.save(person);
    }

    public List<Person> findAll(){
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
                persons.add(new Person(id, fName, lastName));
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
}
