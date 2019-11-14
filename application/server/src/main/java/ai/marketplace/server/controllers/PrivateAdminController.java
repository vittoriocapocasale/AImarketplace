package ai.marketplace.server.controllers;


import ai.marketplace.server.authorities.AdminAuthority;
import ai.marketplace.server.authorities.ClientAuthority;
import ai.marketplace.server.common.*;
import ai.marketplace.server.events.EntityCreatedEvent;
import ai.marketplace.server.events.ResponseReadyEvent;
import ai.marketplace.server.services.ClientServiceInterface;
import ai.marketplace.server.services.UserServiceInterface;
import ai.marketplace.server.structures.Client;
import ai.marketplace.server.structures.Position;
import ai.marketplace.server.structures.RegistrationClient;
import ai.marketplace.server.structures.RegistrationUser;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PrivateAdminController {

    @Autowired
    private ClientServiceInterface clientService;

    @Autowired
    private UserServiceInterface userService;

    @Autowired
    private FileLogger logger;

    @Autowired
    private ApplicationEventPublisher eventPublisher;



    //adds client to the system
    //201 client added
    //400 missing clientId/secret
    //409 clientId already present
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/clients", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDetails addClient(@RequestBody RegistrationClient c, HttpServletResponse response) throws BadRequestException, ConflictException, InternalServerErrorException
    {
        try {
            if(c==null ||c.getClientId()==null||c.getSecret()==null)
            {
                throw new BadRequestException();
            }
            ClientDetails client=clientService.insertNewClient(c);
            eventPublisher.publishEvent(new EntityCreatedEvent(this, response, client.getClientId()));
            return client;
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




    //200 get client details from the system
    //404 client does not exists
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/clients/{client}", method = RequestMethod.GET)
    @ResponseBody
    public ClientDetails getClient(@PathVariable("client") String client,
                                   HttpServletResponse response) throws NotFoundException, InternalServerErrorException
    {
        try {
            ClientDetails c=clientService.loadClientByClientId(client);
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return c;
        }
        catch (ClientRegistrationException ce)
        {
            throw new NotFoundException();
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }


    }



    //201 admin added to the system
    //400 missing username/password
    //409 username already present
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admins", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationUser addAdmin(@RequestBody RegistrationUser user, HttpServletResponse response) throws BadRequestException, ConflictException, InternalServerErrorException
    {
        AdminAuthority aa= new AdminAuthority();
        try {
            if(user==null||user.getUsername()==null||user.getPassword()==null)
            {
                throw new BadRequestException();
            }
            user.setRole(aa.getAuthority());
            UserDetails u=userService.insertNewUser(user);
            String role=u.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse(null);
            RegistrationUser ru=new RegistrationUser( u.getUsername(), null, role, null);
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



    //200 get admin information
    //403 accessing admin informations with other admin credentials
    //404 admin does not exist
    //409 clientId already present
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admins/{admin}", method = RequestMethod.GET)
    @ResponseBody
    public RegistrationUser getAdmin(@PathVariable("admin") String admin,
                                     OAuth2Authentication oAuth2Authentication,
                                     HttpServletResponse response) throws ForbiddenException, NotFoundException, InternalServerErrorException
    {
        try {
            String username=oAuth2Authentication.getUserAuthentication().getName();
            if (!(admin.equals(username)))
            {
                throw new ForbiddenException();
            }
            UserDetails u=userService.loadUserByUsername(admin);
            String role=u.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse("");
            RegistrationUser ru=new RegistrationUser( u.getUsername(), null, role, null);
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return ru;
        }
        catch (ForbiddenException fe)
        {
            throw new ForbiddenException();
        }
        catch (UsernameNotFoundException une)
        {
            throw new NotFoundException();
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }


    }
}
