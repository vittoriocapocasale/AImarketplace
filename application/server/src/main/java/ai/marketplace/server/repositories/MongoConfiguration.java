package ai.marketplace.server.repositories;

import ai.marketplace.server.propertes.DBproperties;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.MongoTransactionManager;

//configures mongo to use teansactions over a replica set

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Autowired
    DBproperties properties;

    //needed to enable transactions
    @Bean
    MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    //creates mongo connection url
    @Override
    public MongoClient mongoClient() {
        StringBuilder s= new StringBuilder("mongodb://");

        s.append(properties.getDBusername())
                .append(":")
                .append(properties.getDBpassword())
                .append("@")
                .append(properties.getDBhost())
                .append(":")
                .append(properties.getDBport())
                .append("/")
                .append(properties.getDBdatabase());
        MongoClientURI uri = new MongoClientURI(s.toString());
        return new MongoClient(uri);
        /*ServerAddress address=new ServerAddress(properties.getDBhost(),properties.getDBport());
        MongoCredential credential=MongoCredential.createCredential(properties.getDBusername(),properties.getDBdatabase(),properties.getDBpassword().toCharArray());
        MongoClientOptions ops=MongoClientOptions.builder().sslEnabled(false).build();
        return new MongoClient(address,credential,ops);*/
    }

    @Override
    protected String getDatabaseName() {
        return properties.getDBdatabase();
    }

    @Override
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(),properties.getDBdatabase());
    }
}