package ai.marketplace.server.services;

import ai.marketplace.server.common.ConflictException;
import ai.marketplace.server.common.NotFoundException;
import ai.marketplace.server.structures.Client;
import ai.marketplace.server.structures.RegistrationClient;
import org.bson.types.ObjectId;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ClientServiceInterface
{
    ClientDetails loadClientByClientId(String s) throws ClientRegistrationException;
    List<String> loadAllClientId();
    ClientDetails insertNewClient(RegistrationClient client) throws ConflictException;
    ClientDetails deleteClient(String id) throws NotFoundException;

}
