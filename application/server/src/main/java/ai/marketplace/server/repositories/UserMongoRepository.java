package ai.marketplace.server.repositories;

import ai.marketplace.server.structures.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


//user repository based on the spring implementation
@Repository
public interface UserMongoRepository extends MongoRepository<User,ObjectId>, UserMongoRepositoryCustom
{
    User findUserByUsername(String username);
}
