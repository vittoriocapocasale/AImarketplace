package ai.marketplace.server.repositories;

import org.bson.types.ObjectId;

import java.util.List;


//used to extend client repository with non-standard methods
public interface ClientMongoRepositoryCustom {
    List<String> getAllClientId();
}
