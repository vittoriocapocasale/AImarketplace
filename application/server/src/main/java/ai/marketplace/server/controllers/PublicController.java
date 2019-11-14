package ai.marketplace.server.controllers;


import ai.marketplace.server.authorities.UserAuthority;
import ai.marketplace.server.common.BadRequestException;
import ai.marketplace.server.common.ConflictException;
import ai.marketplace.server.common.FileLogger;
import ai.marketplace.server.common.InternalServerErrorException;
import ai.marketplace.server.events.EntityCreatedEvent;
import ai.marketplace.server.events.ResponseReadyEvent;
import ai.marketplace.server.services.UserServiceInterface;
import ai.marketplace.server.structures.RegistrationUser;
import ai.marketplace.server.structures.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class PublicController
{
    @Autowired
    private UserServiceInterface service;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private FileLogger logger;



    //200 does nothing but returning urls (link headers) to discover the service
    @RequestMapping(value = "/api", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void discoverService(HttpServletResponse response) throws InternalServerErrorException
    {
        try {
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }


    //200 get the list of usernames registered in the system
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getUsers(HttpServletResponse response) throws InternalServerErrorException
    {
        try {
            List<String> users= service.loadAllUsername();
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return users;
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }


    //201 Register a new User
    //400 missing username password
    //409 username already used
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationUser newUser(@RequestBody RegistrationUser user, HttpServletResponse response) throws ConflictException, BadRequestException, InternalServerErrorException
    {
        UserAuthority ua=new UserAuthority();
        try {
            if(user==null||user.getUsername()==null||user.getPassword()==null)
            {
                throw new BadRequestException();
            }
            user.setRole(ua.getAuthority());
            UserDetails u=service.insertNewUser(user);
            String role=u.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse("");
            RegistrationUser ru=new RegistrationUser(u.getUsername(), "", role, "");
            System.out.println(ru);
            eventPublisher.publishEvent(new EntityCreatedEvent(this, response, user.getUsername()));
            return ru;
        }
        catch (BadRequestException bre)
        {
            throw new BadRequestException();
        }
        catch (ConflictException ce)
        {
            throw new ConflictException();
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }
}
