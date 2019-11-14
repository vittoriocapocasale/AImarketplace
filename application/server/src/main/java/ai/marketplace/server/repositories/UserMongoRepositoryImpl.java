package ai.marketplace.server.repositories;

import ai.marketplace.server.authorities.AdminAuthority;
import ai.marketplace.server.authorities.UserAuthority;
import ai.marketplace.server.structures.*;
import com.mongodb.BasicDBObjectBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


//implementation of non standard methods for user repository
@Repository
public class UserMongoRepositoryImpl implements UserMongoRepositoryCustom
{
    @Autowired
    private MongoTemplate mongoTemplate;



    //increments the user defined by username credit of value
    @Override
    public Long incrementUserCredit(String username, Long value) {
        Query query=new Query();
        query.addCriteria(Criteria.where("username").is(username));
        Update update=new Update();
        update.inc("credit", value);
        Long updated =mongoTemplate.updateFirst(query,update,User.class).getModifiedCount();
        return updated;
    }

    // puts credit of user to 0
    @Override
    public Long resetUserCredit(String username) {
        Query query=new Query();
        query.addCriteria(Criteria.where("username").is(username));
        Update update=new Update();
        update.set("credit", 0);
        Long updated =mongoTemplate.updateFirst(query,update,User.class).getMatchedCount();
        return updated;
    }

    // add archives to the one the user has bought
    @Override
    public void updateUserBoughtArchives(String username, List<SubdocumentArchive> archives) {

        Query query=new Query();
        query.addCriteria(Criteria.where("username").is(username));
        Update update=new Update();
        update.push("boughtArchives", BasicDBObjectBuilder.start("$each", archives).get());
        mongoTemplate.updateFirst(query,update,User.class);

    }

    // get the name of users (not admin) in the system
    @Override
    public List<String> findAllUsernames() {
        ProjectionOperation projection = project("username", "authorities").andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(projection);
        AggregationResults<User> result = mongoTemplate.aggregate(aggregation, "users", User.class);
        return result.getMappedResults().stream()
                .filter(
                        e->{
                            UserAuthority aa=new UserAuthority();
                            for(GrantedAuthority a:e.getAuthorities())
                            {
                                if(a.getAuthority().equals(aa.getAuthority()))
                                {
                                    return true;
                                }
                            }
                            return false;
                        }
                ).map(User::getUsername).collect(Collectors.toList());//.getMappedResults();
    }

    // get the names of all admins (not users) in the systems
    @Override
    public List<String> findAllAdminnames() {
        ProjectionOperation projection = project("username", "authorities").andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(projection);
        AggregationResults<User> result = mongoTemplate.aggregate(aggregation, "users", User.class);
        return result.getMappedResults().stream()
                .filter(
                        e->{
                            AdminAuthority aa=new AdminAuthority();
                            for(GrantedAuthority a:e.getAuthorities())
                            {
                                if(a.getAuthority().equals(aa.getAuthority()))
                                {
                                    return true;
                                }
                            }
                            return false;
                        }
            ).map(User::getUsername).collect(Collectors.toList());//.getMappedResults();
    }

    //returns the tag names of bought archives among the ones provides
    @Override
    public List<String> findAllOwnedArchivesTagName(String username, List<String> archives) {
        Criteria c= Criteria.where("username").is(username);
        if(archives!=null)
        {
            c.and("boughtArchives.tagName").in(archives);
        }
        UnwindOperation unwind = unwind("boughtArchives");
        MatchOperation select = match(c);
        ProjectionOperation projection = project("boughtArchives.tagName").andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(unwind, select, projection);
        AggregationResults<SubdocumentArchive> result = mongoTemplate.aggregate(aggregation, "users", SubdocumentArchive.class);
        return result.getMappedResults().stream().map(SubdocumentArchive::getTagName).collect(Collectors.toList());
    }

    //returns the archives names bought by username user
    @Override
    public List<Archive> loadAllArchivesTagNameByUser(String username) {
        Criteria c= Criteria.where("username").is(username);
        MatchOperation select = match(c);
        UnwindOperation unwind = unwind("boughtArchives");
        ProjectionOperation projection = project("boughtArchives.tagName").andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(select, unwind, projection);
        AggregationResults<SubdocumentArchive> result = mongoTemplate.aggregate(aggregation, "users", SubdocumentArchive.class);
        System.out.println(result.getRawResults());
        return new ArrayList<Archive>(result.getMappedResults());
    }

    //returns the single archive bought by the user and identified by its tagName
    @Override
    public SubdocumentArchive loadArchiveByUserAndTagName(String username, String archiveName) {
        System.out.println("ERERER"+archiveName);
        Criteria c= Criteria.where("username").is(username).and("boughtArchives.tagName").is(archiveName);
        UnwindOperation unwind= unwind("boughtArchives");
        MatchOperation select = match(c);
        ProjectionOperation projection = project()
                .andExpression("boughtArchives").as("archive")
                .andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(unwind, select, projection);
        AggregationResults<SingleArchive> result = mongoTemplate.aggregate(aggregation, "users", SingleArchive.class);
        System.out.println("E"+result.getRawResults());
        return result.getUniqueMappedResult().getArchive();
    }
}
