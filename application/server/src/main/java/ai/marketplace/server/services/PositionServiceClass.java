package ai.marketplace.server.services;

import ai.marketplace.server.common.ConflictException;
import ai.marketplace.server.common.ForbiddenException;
import ai.marketplace.server.common.NotFoundException;
import ai.marketplace.server.repositories.PositionMongoRepository;
import ai.marketplace.server.structures.Archive;
import ai.marketplace.server.structures.DocumentArchive;
import ai.marketplace.server.structures.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional

public class PositionServiceClass implements PositionServiceInterface
{
    @Autowired
    private PositionMongoRepository repo;


    //Service wrapper for repository method
    @Override
    public Archive addArchive(DocumentArchive archive) throws ConflictException {
        DocumentArchive a=null;
        try
        {
            //System.out.println("positions"+archive.getPositions().size());
            a=repo.insert(archive);
        }
        catch (DataIntegrityViolationException e)
        {
            throw new ConflictException();
        }
        return a;

    }

    //try loadArchive. If success the archive exists and can be deleted
    @Override
    public void deleteArchive(String user, String archive) throws ForbiddenException, NotFoundException {
        this.loadArchive(user, archive);
        ArrayList<String> archives = new ArrayList<>();
        archives.add(archive);
        repo.updateArchiveDeleted(archives);
    }

    //Service wrapper for repository method
    @Override
    public List<Position> getTimestampRepresentation(GeoJsonPolygon polygon, Long start, Long end, List<String> users) {
        return repo.selectPositionsTimestampApproximationInAndBetweenByUser(polygon, start, end, users);
    }

    //Service wrapper for repository method
    @Override
    public List<Position> getLatLongRepresentation(GeoJsonPolygon polygon, Long start, Long end, List<String> users) {
        return repo.selectPositionsLatLongApproximationInAndBetweenByUser(polygon, start, end, users);

    }

    //Service wrapper for repository method
    @Override
    public Long getPositionCount(GeoJsonPolygon polygon, Long start, Long end, List<String> users) {
        return repo.selectPositionCount(polygon, start, end, users);

    }

    //Service wrapper for repository method
    @Override
    public List<String> getSearchedArchives(GeoJsonPolygon polygon, Long start, Long end, List<String> users) {
        return repo.selectTagNamesInAndBetweenByUser(polygon, start, end, users);
    }

    //Service wrapper for repository method
    @Override
    public List<Archive> loadAllArchivesTagNameByUser(String username) {
        return repo.loadAllArchivesTagNameByUser(username);
    }

    //Service wrapper for repository method
    @Override
    public Archive loadArchive(String username, String archiveName) throws NotFoundException, ForbiddenException {
        Archive a=repo.findArchiveByTagNameAndDeleted(archiveName, false);
        if (a==null)
        {
            throw new NotFoundException();
        }
        if(!a.getOwner().equals(username))
        {
            throw new ForbiddenException();
        }
        return a;
    }
}
