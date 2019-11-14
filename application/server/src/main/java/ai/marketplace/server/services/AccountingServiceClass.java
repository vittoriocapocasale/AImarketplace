package ai.marketplace.server.services;

import ai.marketplace.server.common.ConflictException;
import ai.marketplace.server.repositories.PositionMongoRepository;
import ai.marketplace.server.repositories.UserMongoRepository;
import ai.marketplace.server.structures.Archive;
import ai.marketplace.server.structures.DocumentArchive;
import ai.marketplace.server.structures.SubdocumentArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional

public class AccountingServiceClass implements AccountingServiceInterface
{
    @Autowired private PositionMongoRepository posRepo;
    @Autowired private UserMongoRepository userRepo;


    //performs the transaction of buying archives. In particular:
    //adds archives to the user's boughtArchives
    //updates archives bought count
    //update archives' owners current credit
    @Override
    public void buyArchives(String username, List<String> archives) throws ConflictException {
        List<String> ownedArchives= userRepo.findAllOwnedArchivesTagName(username, archives);
        List<String> missingArchives=archives.stream().filter(e->!ownedArchives.contains(e)).collect(Collectors.toList());
        List<SubdocumentArchive> buyArch= posRepo.selectUndeletedArchivesByTagName(missingArchives);
        if(buyArch.size() != missingArchives.size())
        {
            throw new ConflictException();
        }
        userRepo.updateUserBoughtArchives(username, buyArch);
        Long c=posRepo.updateArchiveBoughtTimes(missingArchives);
        if(c!=missingArchives.size())
        {
            throw new ConflictException();
        }
        for (Archive arch:buyArch)
        {
            userRepo.incrementUserCredit(arch.getOwner(), Long.valueOf(arch.getPrice()));
        }
    }
    /*
    @Override
    public void sellArchive(DocumentArchive archive) throws ConflictException {
        ArrayList<SubdocumentArchive> archiveList=new ArrayList<>();
        archiveList.add(archive);
        try
        {
            posRepo.insert(archive);
            userRepo.updateUserBoughtArchives(archive.getOwner(), archiveList);
        }
        catch (DataIntegrityViolationException e)
        {
            throw new ConflictException();
        }

    }*/
}
