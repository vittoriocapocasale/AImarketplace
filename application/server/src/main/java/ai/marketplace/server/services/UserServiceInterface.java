package ai.marketplace.server.services;

import ai.marketplace.server.common.BadRequestException;
import ai.marketplace.server.common.ConflictException;
import ai.marketplace.server.common.NotFoundException;
import ai.marketplace.server.structures.Archive;
import ai.marketplace.server.structures.DocumentArchive;
import ai.marketplace.server.structures.RegistrationUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface UserServiceInterface
{
    UserDetails loadUserByUsername(String s) throws UsernameNotFoundException;
    List<String> loadAllUsername();
    List<String> findAllAdminname();
    UserDetails insertNewUser(RegistrationUser user) throws BadRequestException, ConflictException;
    List<Archive> loadAllArchivesTagNameByUser(String username);
    Archive loadArchiveByUserAndTagName(String username, String archiveName) throws NotFoundException;
    void resetBalance(String username) throws NotFoundException;
    long getBalance(String username) throws NotFoundException;

}
