package com.kunnchan.aerospikepoc.ducuments;
/*
 * Created by kunnchan on 22/10/2021
 * package :  com.kunnchan.aerospikepoc.ducuments
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;

@Value
@Document(collection = "person")
@Builder(toBuilder = true)
@AllArgsConstructor
public class Person {

    @Id
    String id;
    String firstName;
    String lastName;
}
