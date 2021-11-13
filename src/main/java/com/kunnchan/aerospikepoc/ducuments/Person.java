package com.kunnchan.aerospikepoc.ducuments;
/*
 * Created by kunnchan on 22/10/2021
 * package :  com.kunnchan.aerospikepoc.ducuments
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;

@Data
@Document(collection = "person")
@Builder(toBuilder = true)
@AllArgsConstructor
public class Person {

    @Id
    String id;
    String firstName;
    String lastName;
    String address;

//    public Boolean getProgrammer() {
//        return isProgrammer != null;
//    }




//    @JsonProperty(value="isProgrammer")
//    public boolean isProgrammer() {
//        return isProgrammer;
//    }
}