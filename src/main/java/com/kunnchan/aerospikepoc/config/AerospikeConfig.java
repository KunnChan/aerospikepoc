package com.kunnchan.aerospikepoc.config;

import com.aerospike.client.Host;
import com.aerospike.client.policy.AuthMode;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.TlsPolicy;
import com.kunnchan.aerospikepoc.repository.PersonRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;

/*
 * Created by kunnchan on 22/10/2021
 * package :  com.kunnchan.aerospikepoc.config
 */
@Configuration
@EnableConfigurationProperties(AerospikeConfig.AerospikeConfigurationProperties.class)
@EnableAerospikeRepositories(basePackageClasses = PersonRepository.class)
public class AerospikeConfig extends AbstractAerospikeDataConfiguration {

    @Autowired
    private AerospikeConfigurationProperties properties;

    @Override
    protected Collection<Host> getHosts() {
        return Host.parseServiceHosts(properties.getHosts());
    }

    @Override
    protected String nameSpace() {
        return properties.getNamespace();
    }

//    @Override
//    protected ClientPolicy getClientPolicy(){
//        ClientPolicy clientPolicy = new ClientPolicy();
//        clientPolicy.failIfNotConnected = true;
//        clientPolicy.tlsPolicy = new TlsPolicy();
//        clientPolicy.authMode = AuthMode.EXTERNAL_INSECURE;
//        clientPolicy.user = properties.username;
//        clientPolicy.password = properties.password;
//        return clientPolicy;
//    }

    @Data
   // @Validated
    @ConfigurationProperties("aerospike")
    public static class AerospikeConfigurationProperties {
        @NotEmpty
        private String hosts;
        @NotEmpty
        private String namespace;

        @NotEmpty
        private String username;
        @NotEmpty
        private String password;

        @NotEmpty
        private String setName;
    }
}
