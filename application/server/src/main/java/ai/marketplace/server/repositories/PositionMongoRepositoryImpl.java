package ai.marketplace.server.repositories;


import ai.marketplace.server.structures.Archive;
import ai.marketplace.server.structures.DocumentArchive;
import ai.marketplace.server.structures.Position;
import ai.marketplace.server.structures.SubdocumentArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.management.remote.SubjectDelegationPermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


//implementation of non standard methods in position/archives repository
@Repository
public class PositionMongoRepositoryImpl implements PositionMongoRepositoryCustom
{
    @Autowired
    private MongoTemplate mongoTemplate;



    //all the positions in the TimeArea, approximated by time
    @Override
    public List<Position> selectPositionsTimestampApproximationInAndBetweenByUser(GeoJsonPolygon polygon, Long start, Long end, List<String> users)
    {
        Criteria criteria=Criteria.where("deleted").is(false);
        if (users != null)
        {
            criteria=criteria.and("owner").in(users);
        }
        if (polygon!=null)
        {
            criteria=criteria.and("positions.coordinates").intersects(polygon);
        }
        criteria=criteria.andOperator(Criteria.where("positions.mark").gte(start), Criteria.where("positions.mark").lte(end));
        UnwindOperation unwind = unwind("positions");
        MatchOperation selectValPos = match(criteria);
        SortOperation sort= sort (Sort.Direction.ASC, "positions.roundedMark");
        GroupOperation groupValPos = group("positions.roundedMark")
                .first("positions.latitude").as("latitude")
                .first("positions.longitude").as("longitude")
                .first("owner").as("positionOwner")
                .first("positions.roundedMark").as("mark");
        ProjectionOperation projection=project("latitude", "positionOwner", "longitude", "mark")
                .andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(unwind, selectValPos, sort, groupValPos, projection);
        AggregationResults<Position> result = mongoTemplate.aggregate(aggregation, "archives", Position.class);
        System.out.println("Uo:"+result.getMappedResults().size());
        return result.getMappedResults();

    }


    //all the positions in the TimeArea, approximated by Lat Long
    @Override
    public List<Position> selectPositionsLatLongApproximationInAndBetweenByUser(GeoJsonPolygon polygon, Long start, Long end, List<String> users)
    {
        Criteria criteria=Criteria.where("deleted").is(false);
        if (users != null)
        {
            criteria=criteria.and("owner").in(users);
        }
        if (polygon!=null)
        {
            criteria=criteria.and("positions.coordinates").intersects(polygon);
        }
        criteria=criteria.andOperator(Criteria.where("positions.mark").gte(start), Criteria.where("positions.mark").lte(end));
        UnwindOperation unwind = unwind("positions");
        MatchOperation selectValPos = match(criteria);
        SortOperation sort= sort (Sort.Direction.ASC, "positions.roundedLongitude", "positions.roundedLatitude");
        GroupOperation groupValPos = group("positions.roundedLongitude", "positions.roundedLatitude")
                .first("positions.mark").as("mark")
                .first("owner").as("positionOwner")
                .first("positions.roundedLongitude").as("longitude")
                .first("positions.roundedLatitude").as("latitude");
        ProjectionOperation projection=project("mark", "positionOwner", "latitude", "longitude")
                .andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(unwind, selectValPos, sort, groupValPos, projection);
        AggregationResults<Position> result = mongoTemplate.aggregate(aggregation, "archives", Position.class);
        return result.getMappedResults();

    }


    //count real number of position in timeArea
    @Override
    public Long selectPositionCount(GeoJsonPolygon polygon, Long start, Long end, List<String> users) {
        Criteria criteria=Criteria.where("deleted").is(false);
        if (users != null)
        {
            criteria=criteria.and("owner").in(users);
        }
        if (polygon!=null)
        {
            criteria=criteria.and("positions.coordinates").intersects(polygon);
        }
        criteria=criteria.andOperator(Criteria.where("positions.mark").gte(start), Criteria.where("positions.mark").lte(end));
        UnwindOperation unwind = unwind("positions");
        MatchOperation selectValPos = match(criteria);
        CountOperation countOperation= count().as("mark");
        Aggregation aggregation = Aggregation.newAggregation(unwind, selectValPos, countOperation);
        AggregationResults<Position> result = mongoTemplate.aggregate(aggregation, "archives", Position.class);
        if(result.getUniqueMappedResult()!=null && result.getUniqueMappedResult().getMark()!=null)
        {
            return result.getUniqueMappedResult().getMark();
        }
        return 0L;
    }

    //all the archives with at least a position in the TimeArea
    @Override
    public List<String> selectTagNamesInAndBetweenByUser(GeoJsonPolygon polygon, Long start, Long end, List<String> users) {
        Criteria criteria=Criteria.where("deleted").is(false);
        if (users != null)
        {
            criteria=criteria.and("owner").in(users);
        }
        if (polygon!=null)
        {
            criteria=criteria.and("positions.coordinates").intersects(polygon);
        }
        criteria=criteria.andOperator(Criteria.where("positions.mark").gte(start), Criteria.where("positions.mark").lte(end));
        UnwindOperation unwind = unwind("positions");
        MatchOperation selectValpos = match(criteria);
        GroupOperation countValPos = group("tagName").max("tagName").as("tagName");
        ProjectionOperation projection = project("tagName").andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(unwind, selectValpos, countValPos, projection);
        AggregationResults<DocumentArchive> result = mongoTemplate.aggregate(aggregation, "archives", DocumentArchive.class);
        System.out.println(result.getRawResults());
        return result.getMappedResults().stream().map(DocumentArchive::getTagName).collect(Collectors.toList());
    }



    //gets archives with the given tagName
    @Override
    public List<SubdocumentArchive> selectArchivesByTagName(List<String> archives){
        Criteria criteria=Criteria.where("tagName").in(archives);
        MatchOperation selectValpos = match(criteria);
        Aggregation aggregation = Aggregation.newAggregation(selectValpos);
        AggregationResults<SubdocumentArchive> result = mongoTemplate.aggregate(aggregation, "archives", SubdocumentArchive.class);
        return result.getMappedResults();
    }


    //select archives with the given tagName and not deleted
    @Override
    public List<SubdocumentArchive> selectUndeletedArchivesByTagName(List<String> archives){
        Criteria criteria=Criteria.where("tagName").in(archives).and("deleted").is(false);
        MatchOperation selectValpos = match(criteria);
        ProjectionOperation projection= project().andExclude("_id").andExclude("positions._id");
        Aggregation aggregation = Aggregation.newAggregation(selectValpos, projection);
        AggregationResults<SubdocumentArchive> result = mongoTemplate.aggregate(aggregation, "archives", SubdocumentArchive.class);
        return result.getMappedResults();
    }

    //increases by 1 the number of times archives have been bought
    @Override
    public Long updateArchiveBoughtTimes (List<String> archives)
    {
        Query query=new Query();
        query.addCriteria(Criteria.where("tagName").in(archives));
        Update update=new Update();
        update.inc("soldTimes", 1);
        Long updated =mongoTemplate.updateMulti(query,update,DocumentArchive.class).getModifiedCount();
        return updated;
    }

    //deletes some archives (deleted=true)
    @Override
    public Long updateArchiveDeleted(List<String> archives) {
        Query query=new Query();
        query.addCriteria(Criteria.where("tagName").in(archives));
        Update update=new Update();
        update.set("deleted", true);
        Long updated =mongoTemplate.updateMulti(query,update,DocumentArchive.class).getModifiedCount();
        return updated;
    }

    //gets minimal information (tagName, soldTimes) from the ones published by the user
    @Override
    public List<Archive> loadAllArchivesTagNameByUser(String username) {
        Criteria c= Criteria.where("deleted").is(false).and("owner").is(username);
        MatchOperation select= match(c);
        ProjectionOperation projection = project("tagName", "soldTimes");
        Aggregation aggregation = Aggregation.newAggregation(select, projection);
        AggregationResults<DocumentArchive> result = mongoTemplate.aggregate(aggregation, "archives", DocumentArchive.class);
        return new ArrayList<>(result.getMappedResults());
    }
}


