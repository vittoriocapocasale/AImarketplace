package ai.marketplace.server.repositories;


import ai.marketplace.server.structures.Archive;
import ai.marketplace.server.structures.DocumentArchive;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


//Positions/archives repository that uses sprng implementation
@Repository
public interface PositionMongoRepository extends MongoRepository<DocumentArchive, ObjectId>, PositionMongoRepositoryCustom
{
    Archive findArchiveByTagNameAndDeleted(String tagName, boolean deleted);
}
