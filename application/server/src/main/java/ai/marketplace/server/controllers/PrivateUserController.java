package ai.marketplace.server.controllers;

import ai.marketplace.server.common.*;
import ai.marketplace.server.events.EntityCreatedEvent;
import ai.marketplace.server.events.ResponseReadyEvent;
import ai.marketplace.server.services.AccountingServiceInterface;
import ai.marketplace.server.services.PositionServiceInterface;
import ai.marketplace.server.services.UserServiceInterface;
import ai.marketplace.server.structures.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@RestController
public class PrivateUserController
{
    @Autowired
    private PositionServiceInterface positionService;

    @Autowired
    private UserServiceInterface userService;

    @Autowired
    private AccountingServiceInterface accountingService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private FileLogger logger;

    @Autowired
    private TimeAreaConverter timeAreaConverter;

    //200 Get current user info
    //403 user info do not belong to the authenticated user
    //404 user does not exists
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/users/{username}", method = RequestMethod.GET)
    @ResponseBody
    public RegistrationUser getUser(@PathVariable("username") String user,
                                    OAuth2Authentication oAuth2Authentication,
                                    HttpServletResponse response) throws ForbiddenException, NotFoundException, InternalServerErrorException
    {
        try {
            String username=oAuth2Authentication.getUserAuthentication().getName();
            if (!(user.equals(username)))
            {
                throw new ForbiddenException();
            }
            UserDetails u=userService.loadUserByUsername(username);
            String role=u.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse("");
            RegistrationUser ru=new RegistrationUser(u.getUsername(), "", role, "");
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return ru;
        }
        catch (ForbiddenException fe)
        {
            throw new ForbiddenException();
        }
        catch (UsernameNotFoundException ue)
        {
            throw new NotFoundException();
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }


    //200 get user credit
    //403 the credit does not belong to the authenticated user
    //404 user does not exist
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/users/{username}/credit", method = RequestMethod.GET)
    @ResponseBody
    public Long GetBalance(@PathVariable("username") String user,
                             OAuth2Authentication oAuth2Authentication,
                             HttpServletResponse response) throws ForbiddenException, NotFoundException, InternalServerErrorException
    {
        try {
            String username=oAuth2Authentication.getUserAuthentication().getName();
            if (!(user.equals(username)))
            {
                throw new ForbiddenException();
            }
            Long balance= userService.getBalance(username);
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return balance;
        }
        catch (ForbiddenException fe)
        {
            throw new ForbiddenException();
        }
        catch (NotFoundException nfe)
        {
            throw new NotFoundException();
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }


    //204 update the user credit
    //403 the credit does not belong to the authenticated user
    //404 user does not exist
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/users/{username}/credit", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ResetBalance(@PathVariable("username") String user,
                                        OAuth2Authentication oAuth2Authentication,
                                        HttpServletResponse response) throws ForbiddenException, NotFoundException, InternalServerErrorException
    {
        try {
            String username=oAuth2Authentication.getUserAuthentication().getName();
            if (!(user.equals(username)))
            {
                throw new ForbiddenException();
            }
            userService.resetBalance(username);
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
        }
        catch (ForbiddenException fe)
        {
            throw new ForbiddenException();
        }
        catch (NotFoundException nfe)
        {
            throw new NotFoundException();
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }

    //200 Get the name and bought times of all the archives published on the marketplace by this user (and not deleted)
    //403 the authenticated user is not the publisher
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/users/{username}/soldArchives", method = RequestMethod.GET)
    @ResponseBody
    public List<Archive> getSoldArchives(@PathVariable("username") String user,
                                        OAuth2Authentication oAuth2Authentication,
                                        HttpServletResponse response) throws ForbiddenException, InternalServerErrorException
    {
        try {
            String username=oAuth2Authentication.getUserAuthentication().getName();
            if (!(user.equals(username)))
            {
                throw new ForbiddenException();
            }
            List<Archive> archives= positionService.loadAllArchivesTagNameByUser(username);
            System.out.println(archives);
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return archives;
        }
        catch (ForbiddenException fe)
        {
            throw new ForbiddenException();
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }


    //201 Add archive to the marketplace
    //400 missing data in the input
    //403 authenticated user is not path user
    //409 the archive name does already exist
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/users/{username}/soldArchives", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Archive addArchive(@PathVariable("username") String user,
                              @RequestBody DocumentArchive archive,
                              OAuth2Authentication oAuth2Authentication,
                              HttpServletResponse response) throws BadRequestException, ForbiddenException,InternalServerErrorException, ConflictException
    {
        try {
            String username=oAuth2Authentication.getUserAuthentication().getName();
            if (!(user.equals(username)))
            {
                throw new ForbiddenException();
            }
            if(archive==null)
            {
                throw new BadRequestException();
            }
            archive.setSoldTimes(0L);
            archive.setDeleted(false);
            archive.setOwner(username);
            ArrayList<Position> bestList=new ArrayList<>();
            for(Position p:archive.getPositions())
            {
                if(p.valid())
                {
                    p.setPositionOwner(username);
                    p.updateCoordinates();
                    bestList.add(p);
                }
            }
            //ArrayList<Position> bestList=Functions.findMaximumCompatiblePositions(archive.getPositions());
            archive.setPositions(bestList);
            Archive a=positionService.addArchive(archive);
            eventPublisher.publishEvent(new EntityCreatedEvent(this, response, archive.getTagName()));
            return a;
        }
        catch (BadRequestException bre)
        {
            throw new BadRequestException();
        }
        catch (ForbiddenException fe)
        {
            throw new ForbiddenException();
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

    //200 get the archive (if has been published by this user and it has not been deleted
    //403 authenticated user is not path user
    //404 archive not found

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/users/{username}/soldArchives/{archiveName}", method = RequestMethod.GET)
    @ResponseBody
    public Archive getSoldArchive(@PathVariable("username") String user,
                                          @PathVariable("archiveName") String archiveName,
                                          OAuth2Authentication oAuth2Authentication,
                                          HttpServletResponse response) throws ForbiddenException, NotFoundException, InternalServerErrorException
    {
        try {
            String username=oAuth2Authentication.getUserAuthentication().getName();
            if (!(user.equals(username)))
            {
                throw new ForbiddenException();
            }
            Archive a=positionService.loadArchive(username, archiveName);
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return a;
        }
        catch (ForbiddenException fe)
        {
            throw new ForbiddenException();
        }
        catch (NotFoundException ne)
        {
            throw new NotFoundException();
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }

    //204 Delete archive (other users will not be able to buy it. The archive will remain in the database)
    //403 authenticated user is not path user
    //404 archive not found
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/users/{username}/soldArchives/{archiveName}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSoldArchive(@PathVariable("username") String user,
                                  @PathVariable("archiveName") String archiveName,
                                  OAuth2Authentication oAuth2Authentication,
                                  HttpServletResponse response) throws ForbiddenException,NotFoundException, InternalServerErrorException
    {
        try {
            String username=oAuth2Authentication.getUserAuthentication().getName();
            if (!(user.equals(username)))
            {
                throw new ForbiddenException();
            }
            positionService.deleteArchive(username, archiveName);
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
        }
        catch (ForbiddenException fe)
        {
            throw new ForbiddenException();
        }
        catch (NotFoundException ne)
        {
            throw new NotFoundException();
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }

    //200 Get the  archives bought (tagname only)
    //403 authenticated user is not path user
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/users/{username}/boughtArchives", method = RequestMethod.GET)
    @ResponseBody
    public List<Archive> getBoughtArchives(@PathVariable("username") String user,
                                          OAuth2Authentication oAuth2Authentication,
                                          HttpServletResponse response) throws ForbiddenException, InternalServerErrorException
    {
        try {
            String username=oAuth2Authentication.getUserAuthentication().getName();
            if (!(user.equals(username)))
            {
                throw new ForbiddenException();
            }
            List<Archive> archives=userService.loadAllArchivesTagNameByUser(username);
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return archives;
        }
        catch (ForbiddenException fe)
        {
            throw new ForbiddenException();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }

    //204 purchased archives identified by the submitted names
    //403 path user different from auth user
    //409 one or more archives are deleted
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/users/{username}/boughtArchives", method = RequestMethod.PATCH)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void buyArchives(@PathVariable("username") String user,
                            @RequestBody List<String> archives,
                            OAuth2Authentication oAuth2Authentication,
                            HttpServletResponse response) throws ForbiddenException, ConflictException, InternalServerErrorException
    {
        try {
            String username=oAuth2Authentication.getUserAuthentication().getName();
            if (!(user.equals(username)))
            {
                throw new ForbiddenException();
            }
            if(archives==null)
            {
                archives= new ArrayList<>();
            }
            System.out.println("SSSSS"+archives.size());
            accountingService.buyArchives(username, archives);
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
        }
        catch (ForbiddenException fe)
        {
            throw new ForbiddenException();
        }
        catch (ConflictException be)
        {
            throw new ConflictException();
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }


    }

    //200 get the archive (if it was bought)
    //403 authenticated user is not path user
    //404 archive not found
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/users/{username}/boughtArchives/{archiveName}", method = RequestMethod.GET)
    @ResponseBody
    public Archive getArchive(@PathVariable("username") String user,
                                      @PathVariable("archiveName") String archiveName,
                                      OAuth2Authentication oAuth2Authentication,
                                      HttpServletResponse response) throws  ForbiddenException, NotFoundException, InternalServerErrorException
    {
        try {
            String username=oAuth2Authentication.getUserAuthentication().getName();
            if (!(user.equals(username)))
            {
                throw new ForbiddenException();
            }
System.out.println("MMMM"+archiveName);
            Archive a= userService.loadArchiveByUserAndTagName(username, archiveName);
System.out.println("HHHHHHHH");
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return a;
        }
        catch (ForbiddenException fe)
        {
            throw new ForbiddenException();
        }
        catch (NotFoundException ne)
        {
            throw new NotFoundException();
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }



    //200 Query for positions representations
    //400 Input data is uncomplete/bad-formed
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/marketplace/positions", method = RequestMethod.GET)
    @ResponseBody
    public List<Position> getPositions(@RequestParam(value = "type", defaultValue ="timestamp" , required = false) String type,
                                       @RequestParam(value="filter", required=true) String timeAreaString,
                                       HttpServletResponse response) throws BadRequestException, InternalServerErrorException
    {
        try {
            TimeArea timeArea=null;
            GeoJsonPolygon polygon= null;
            List<Position> result=null;
            List<Point> points=new ArrayList<>();
            try
            {
                String ta=java.net.URLDecoder.decode(timeAreaString, StandardCharsets.UTF_8.name());
                timeArea=timeAreaConverter.convert(ta);
            }
            catch (Throwable t)
            {
                throw new BadRequestException();
            }
            if (timeArea.getFrom() < 0 || timeArea.getFrom() == null) {
                timeArea.setFrom(0L);
            }
            if (timeArea.getTo() < 0 || timeArea.getTo() == null) {
                timeArea.setTo(Long.MAX_VALUE);
            }
            if(timeArea.getUsers()==null)
            {
                timeArea.setUsers(new ArrayList<>());
            }
            if (timeArea.getPolygon() != null) {
                for (Position p : timeArea.getPolygon()) {
                    if (!p.valid())
                    {
                        throw new BadRequestException();
                    }
                    Point point = new Point(p.getLongitude(), p.getLatitude());
                    points.add(point);
                }
                points.add(points.get(0));
                polygon = new GeoJsonPolygon(points);
            }
            if (type.equals("timestamp"))
            {
                result=positionService.getTimestampRepresentation(polygon, timeArea.getFrom(), timeArea.getTo(), timeArea.getUsers());
            }
            else
            {
                result= positionService.getLatLongRepresentation(polygon, timeArea.getFrom(), timeArea.getTo(), timeArea.getUsers());

            }
            System.out.println("Size"+result.size());
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return result;
        }
        catch (BadRequestException be)
        {
            throw be;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }


    //200 get number of positions in the selection
    //400 missing/wrong data in the input
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/marketplace/positions/count", method = RequestMethod.GET)
    @ResponseBody
    public Long getPositions(@RequestParam(value="filter", required=true) String timeAreaString,
                                       HttpServletResponse response) throws BadRequestException, InternalServerErrorException
    {
        try {
            TimeArea timeArea=null;
            GeoJsonPolygon polygon= null;
            Long result=null;
            List<Point> points=new ArrayList<>();
            try
            {
                String ta=java.net.URLDecoder.decode(timeAreaString, StandardCharsets.UTF_8.name());
                timeArea=timeAreaConverter.convert(ta);
            }
            catch (Throwable t)
            {
                throw new BadRequestException();
            }
            if (timeArea.getFrom() < 0 || timeArea.getFrom() == null) {
                timeArea.setFrom(0L);
            }
            if (timeArea.getTo() < 0 || timeArea.getTo() == null) {
                timeArea.setTo(Long.MAX_VALUE);
            }
            if(timeArea.getUsers()==null)
            {
                timeArea.setUsers(new ArrayList<>());
            }
            if (timeArea.getPolygon() != null) {
                for (Position p : timeArea.getPolygon()) {
                    if (!p.valid())
                    {
                        throw new BadRequestException();
                    }
                    Point point = new Point(p.getLongitude(), p.getLatitude());
                    points.add(point);
                }
                points.add(points.get(0));
                polygon = new GeoJsonPolygon(points);
            }
            result= positionService.getPositionCount(polygon, timeArea.getFrom(), timeArea.getTo(), timeArea.getUsers());
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return result;
        }
        catch (BadRequestException be)
        {
            throw be;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }


    //200 list of archives containing positions of interest
    //400 bad/missing input data

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/marketplace/archives", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getArchives(@RequestParam(value="filter", required=true) String timeAreaString, HttpServletResponse response) throws BadRequestException, InternalServerErrorException
    {
        try {
            TimeArea timeArea=null;
            GeoJsonPolygon polygon= null;
            List<Point> points=new ArrayList<>();
            List<String> archives = null;
            try
            {
                String ta=java.net.URLDecoder.decode(timeAreaString, StandardCharsets.UTF_8.name());
                timeArea=timeAreaConverter.convert(ta);
                System.out.println("Empty:"+ta.isEmpty());
            }
            catch (Throwable t)
            {
                throw new BadRequestException();
            }
            if (timeArea.getFrom() < 0 || timeArea.getFrom() == null) {
                timeArea.setFrom(0L);
            }
            if (timeArea.getTo() < 0 || timeArea.getTo() == null) {
                timeArea.setTo(Long.MAX_VALUE);
            }
            if(timeArea.getUsers()==null)
            {
                timeArea.setUsers(new ArrayList<>());
            }
            if (timeArea.getPolygon() != null) {
                for (Position p : timeArea.getPolygon()) {
                    if (!p.valid())
                    {
                        throw new BadRequestException();
                    }
                    Point point = new Point(p.getLongitude(), p.getLatitude());
                    points.add(point);
                }
                points.add(points.get(0));
                polygon = new GeoJsonPolygon(points);
            }
            archives= positionService.getSearchedArchives(polygon, timeArea.getFrom(), timeArea.getTo(), timeArea.getUsers());
            eventPublisher.publishEvent(new ResponseReadyEvent(this, response));
            return archives;
        }
        catch (BadRequestException be)
        {
            throw be;
        }
        catch (Exception e)
        {
            logger.log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }


    //

}
