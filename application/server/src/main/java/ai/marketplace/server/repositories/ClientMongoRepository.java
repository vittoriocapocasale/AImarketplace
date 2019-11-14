package ai.marketplace.server.repositories;

import ai.marketplace.server.structures.Client;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

//client repository that uses spring implementation
@Repository
public interface ClientMongoRepository extends MongoRepository<Client,ObjectId>, ClientMongoRepositoryCustom{
    Client findClientByClientId(String clientId);
    Client deleteClientByClientId(String clientId);
}
