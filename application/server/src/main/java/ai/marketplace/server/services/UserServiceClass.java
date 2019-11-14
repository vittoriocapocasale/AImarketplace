package ai.marketplace.server.services;

import ai.marketplace.server.authorities.AdminAuthority;
import ai.marketplace.server.common.*;
import ai.marketplace.server.structures.*;
import ai.marketplace.server.repositories.UserMongoRepository;
import ai.marketplace.server.authorities.CustomerAuthority;
import ai.marketplace.server.authorities.UserAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional

public class UserServiceClass implements UserDetailsService, UserServiceInterface
{
    @Autowired
    UserMongoRepository repo;
    @Autowired
    PasswordEncoder userPasswordEncoder;


    //Service wrapper for repository method
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User u= repo.findUserByUsername(s);
        if(u==null)
        {
            throw new UsernameNotFoundException("");
        }
        return u;
    }

    //Service wrapper for repository method
    @Override
    public List<String> loadAllUsername() {
        return repo.findAllUsernames();
    }

    //Service wrapper for repository method
    @Override
    public List<String> findAllAdminname() {
        return repo.findAllAdminnames();
    }

    //Insert a new user giving the appropriate authority
    @Override
    public UserDetails insertNewUser(RegistrationUser user) throws  BadRequestException, ConflictException
    {
            UserAuthority ua= new UserAuthority();
            AdminAuthority aa=new AdminAuthority();
            UserDetails u=null;
            if(user.getUsername()==null||user.getPassword()==null)
            {
                throw new BadRequestException();
            }
            User newUser=new User(user.getUsername(),userPasswordEncoder.encode(user.getPassword()));
            Set<String> authorities=new HashSet<>();
            if(ua.getAuthority().equals(user.getRole()))
            {
                authorities.add(ua.getAuthority());
            }
            else if(aa.getAuthority().equals(user.getRole()))
            {
                authorities.add(aa.getAuthority());
            }
            else
            {
                throw new BadRequestException();
            }
            newUser.setAuthorities(authorities);
            try
            {
               u= repo.insert(newUser);
            }
            catch (DataIntegrityViolationException e)
            {
                throw new ConflictException();
            }
            return u;
    }

    //Service wrapper for repository method
    @Override
    public List<Archive> loadAllArchivesTagNameByUser(String username) {
        return repo.loadAllArchivesTagNameByUser(username);
    }

    //Service wrapper for repository method
    @Override
    public Archive loadArchiveByUserAndTagName(String username, String archiveName) throws NotFoundException {
        SubdocumentArchive archive=repo.loadArchiveByUserAndTagName(username, archiveName);
        if(archive==null)
        {
            throw new NotFoundException();
        }
        //System.out.println("VVVVVVVVVVVVVVVVVVV"+archive.getTagName());
        return archive;
    }

    //Service wrapper for repository method
    @Override
    public void resetBalance(String username) throws NotFoundException {
        Long n=repo.resetUserCredit(username);
        if(n==0)
        {
            throw new NotFoundException();
        }
    }

    //Service wrapper for repository method
    @Override
    public long getBalance(String username) throws NotFoundException {
        User u=repo.findUserByUsername(username);
        if(u==null)
        {
            throw new NotFoundException();
        }
        return u.getCredit();
    }
}
