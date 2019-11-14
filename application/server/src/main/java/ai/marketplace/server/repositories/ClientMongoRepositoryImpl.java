package ai.marketplace.server.repositories;

import ai.marketplace.server.structures.Client;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;


//implementation of the non standard methods defined in clientMongoRepositoryCustom

@Repository
public class ClientMongoRepositoryImpl implements ClientMongoRepositoryCustom {

    @Autowired MongoTemplate mongoTemplate;


    //returns the list of client ids registered in the system
    @Override
    public List<String> getAllClientId() {
        ProjectionOperation projection = project("clientId");
        Aggregation aggregation = Aggregation.newAggregation( projection);
        AggregationResults<Client> result = mongoTemplate.aggregate(aggregation, "clients", Client.class);
        return result.getMappedResults().stream().map(Client::getClientId).collect(Collectors.toList());
    }
}
