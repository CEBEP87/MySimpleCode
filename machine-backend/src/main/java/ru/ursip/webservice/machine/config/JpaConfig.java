package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * configuration connection to BD
 *
 * @author samsonov
 * @since 16.10.2017
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine")
@EnableMongoRepositories("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.repository")
public class JpaConfig {

    /**
     * Initiating a data source host
     */
    @Value("${spring.data.mongodb.host}")
    private String host;

    /**
     * Initiating port
     */
    @Value("${spring.data.mongodb.port}")
    private Integer port;
    /**
     * Initiating username
     */

    @Value("${spring.data.mongodb.username}")
    private String username;
    /**
     * Initiating password
     */
    @Value("${spring.data.mongodb.password}")
    private String password;
    /**
     * Initiating database
     */
    @Value("${spring.data.mongodb.database}")
    private String database;

    /**
     * Initiating a data source host
     *
     * @return SimpleMongoDbFactory
     */
    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        MongoClient mongoClient = new MongoClient(host, port);
        UserCredentials userCredentials = new UserCredentials(username, password);
        return new SimpleMongoDbFactory(mongoClient, database, userCredentials);
    }

    /**
     * Initiating resource
     *
     * @return resource
     */
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }
}
