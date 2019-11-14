package ai.marketplace.server;

import ai.marketplace.server.authorities.AdminAuthority;
import ai.marketplace.server.common.ConflictException;
import ai.marketplace.server.common.FileLogger;
import ai.marketplace.server.repositories.ClientMongoRepository;
import ai.marketplace.server.repositories.PositionMongoRepository;
import ai.marketplace.server.repositories.UserMongoRepository;
import ai.marketplace.server.services.ClientServiceClass;
import ai.marketplace.server.services.UserServiceClass;
import ai.marketplace.server.structures.RegistrationClient;
import ai.marketplace.server.structures.RegistrationUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages="ai.marketplace.server")
public class ServerApplication {

    public static void main(String[] args) {
        try{
        ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);
        RegistrationClient c= new RegistrationClient("spring-security-oauth2-read-write-client","spring-security-oauth2-read-write-client-password1234");
        ClientServiceClass cr=context.getBean(ClientServiceClass.class);
        PositionMongoRepository pmr=context.getBean(PositionMongoRepository.class);
        UserMongoRepository umr = context.getBean(UserMongoRepository.class);
        ClientMongoRepository cmr = context.getBean(ClientMongoRepository.class);
        RegistrationUser u =new RegistrationUser("admin", "admin1234", new AdminAuthority().getAuthority(), null);
        UserServiceClass ur =context.getBean(UserServiceClass.class);
        FileLogger logger= context.getBean(FileLogger.class);
        try {
            //pmr.deleteAll();
            //umr.deleteAll();
            //cmr.deleteAll();
            cr.insertNewClient(c);
        }
        catch (ConflictException ce)
        {
            logger.log.warn("Client already initialized, skipping...");
        }
        catch (Exception e)
        {
            logger.log.error("Unexpected exception");
        }
        try {
            ur.insertNewUser(u);
        }
        catch (ConflictException ce)
        {
            logger.log.warn("Default admin already initialized, skipping...");
        }
        catch (Exception e)
        {
            logger.log.error("Unexpected exception");
        }}
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}