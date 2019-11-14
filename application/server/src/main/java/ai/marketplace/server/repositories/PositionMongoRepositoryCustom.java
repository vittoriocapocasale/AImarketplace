package ai.marketplace.server.repositories;

import ai.marketplace.server.structures.Archive;
import ai.marketplace.server.structures.DocumentArchive;
import ai.marketplace.server.structures.Position;
import ai.marketplace.server.structures.SubdocumentArchive;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import java.util.List;


//extended positions/archives repository with non-standard methods
public interface PositionMongoRepositoryCustom
{
    List<Position> selectPositionsTimestampApproximationInAndBetweenByUser(GeoJsonPolygon polygon, Long start, Long end, List<String> users);
    List<Position> selectPositionsLatLongApproximationInAndBetweenByUser(GeoJsonPolygon polygon, Long start, Long end, List<String> users);
    Long selectPositionCount(GeoJsonPolygon polygon, Long start, Long end, List<String> users);
    List<String> selectTagNamesInAndBetweenByUser(GeoJsonPolygon polygon, Long start, Long end, List<String> users);
    List<SubdocumentArchive> selectArchivesByTagName(List<String> archives);
    List<SubdocumentArchive> selectUndeletedArchivesByTagName(List<String> archives);
    Long updateArchiveBoughtTimes(List<String> archives);
    Long updateArchiveDeleted(List<String> archives);
    List<Archive>loadAllArchivesTagNameByUser(String username);

}
