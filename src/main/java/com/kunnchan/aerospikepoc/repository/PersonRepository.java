package com.kunnchan.aerospikepoc.repository;
/*
 * Created by kunnchan on 22/10/2021
 * package :  com.kunnchan.aerospikepoc.repository
 */


import com.kunnchan.aerospikepoc.ducuments.Person;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends AerospikeRepository<Person, String> {
    List<Person> findByFirstName(String firstName);
}
