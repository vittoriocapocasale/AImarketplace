package ai.marketplace.server.services;

import ai.marketplace.server.common.ConflictException;
import ai.marketplace.server.structures.DocumentArchive;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountingServiceInterface
{
    void buyArchives(String username, List<String> archives) throws ConflictException;
}
