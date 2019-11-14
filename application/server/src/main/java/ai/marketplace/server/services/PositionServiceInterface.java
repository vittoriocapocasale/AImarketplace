package ai.marketplace.server.services;

import ai.marketplace.server.common.ConflictException;
import ai.marketplace.server.common.ForbiddenException;
import ai.marketplace.server.common.NotFoundException;
import ai.marketplace.server.structures.Archive;
import ai.marketplace.server.structures.DocumentArchive;
import ai.marketplace.server.structures.Position;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface PositionServiceInterface
{
    Archive addArchive(DocumentArchive archive) throws ConflictException;
    void deleteArchive(String username, String archive) throws ForbiddenException, NotFoundException;
    List<Position> getTimestampRepresentation(GeoJsonPolygon polygon, Long start, Long end, List<String> users);
    List<Position> getLatLongRepresentation(GeoJsonPolygon polygon, Long start, Long end, List<String> users);
    Long getPositionCount(GeoJsonPolygon polygon, Long start, Long end, List<String> users);
    List<String> getSearchedArchives(GeoJsonPolygon polygon, Long start, Long end, List<String> users);
    List<Archive> loadAllArchivesTagNameByUser(String username);
    Archive loadArchive(String username, String archiveName)  throws NotFoundException, ForbiddenException;

}
