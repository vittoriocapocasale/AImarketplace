package ai.marketplace.server.repositories;

import ai.marketplace.server.structures.Archive;
import ai.marketplace.server.structures.DocumentArchive;
import ai.marketplace.server.structures.SubdocumentArchive;

import java.util.List;


//repository containing non-standard methods for users repository
public interface UserMongoRepositoryCustom
{
    Long incrementUserCredit(String username, Long value);
    Long resetUserCredit(String username);
    void updateUserBoughtArchives(String username, List<SubdocumentArchive> archives);
    List<String> findAllUsernames();
    List<String> findAllAdminnames();
    List<String> findAllOwnedArchivesTagName(String username, List<String> archives);
    List<Archive> loadAllArchivesTagNameByUser(String username);
    SubdocumentArchive loadArchiveByUserAndTagName(String username, String archiveName);


}
