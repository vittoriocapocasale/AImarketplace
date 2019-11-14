package ai.marketplace.server.services;

import ai.marketplace.server.common.ConflictException;
import ai.marketplace.server.common.Encoders;
import ai.marketplace.server.repositories.ClientMongoRepository;
import ai.marketplace.server.structures.Client;
import ai.marketplace.server.structures.RegistrationClient;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientServiceClass implements ClientServiceInterface, ClientDetailsService {

    @Autowired
    ClientMongoRepository repo;
    @Autowired
    PasswordEncoder oauthClientPasswordEncoder;

    //Service wrapper for repository method
    @Override
    public ClientDetails loadClientByClientId(String s) throws ClientRegistrationException {
        Client c=repo.findClientByClientId(s);
        if(c==null)
        {
            throw new ClientRegistrationException("");
        }
        return c;
    }

    //Service wrapper for repository method
    @Override
    public List<String> loadAllClientId() {
        return repo.getAllClientId();
    }

    //Service wrapper for repository method
    @Override
    public ClientDetails insertNewClient(RegistrationClient client) throws ConflictException {
        Client c=new Client(client.getClientId(), oauthClientPasswordEncoder.encode(client.getSecret()));
        try {
            c=repo.insert(c);
        }
        catch (DataIntegrityViolationException ie)
        {
            throw new ConflictException();
        }
        return c;

    }

    //Service wrapper for repository method
    @Override
    public Client deleteClient(String id) {

        return repo.deleteClientByClientId(id);
    }
}
