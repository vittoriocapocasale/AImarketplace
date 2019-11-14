package ai.marketplace.server.structures;

import ai.marketplace.server.authorities.ClientAuthority;
import ai.marketplace.server.propertes.GeneralProperties;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.swing.*;
import java.util.*;

//implementation of ClientDetails to automatically store clients

@Document(collection = "clients")
public class Client implements ClientDetails {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String clientId;
    private String secret;
    private Set<String> grantTypes = new HashSet<>();
    private Set<String> authorities = new HashSet<>();
    private Set<String> scopes = new HashSet<>();
    public Client ()
    {
        this.clientId="";
        this.secret="";
        grantTypes.add("password");
        authorities.add("CLIENT");
        scopes.add("read");
        scopes.add("write");
    }
    public Client(String name, String secret)
    {
        this.clientId= name;
        this.secret=secret;
        grantTypes.add("password");
        authorities.add("CLIENT");
        scopes.add("read");
        scopes.add("write");
    }

    public ObjectId getId() {
        return id;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        HashSet<String> resources=new HashSet<>();
        resources.add(GeneralProperties.RESOURCE_ID);
        return resources;
    }

    @Override
    public boolean isSecretRequired() {
        return true;
    }

    @Override
    public String getClientSecret() {
        return secret;
    }

    @Override
    public boolean isScoped() {
        return false;
    }

    @Override
    public Set<String> getScope() {
        return this.scopes;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return this.grantTypes;

    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return null;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> as=new ArrayList<>();
        for(String s:this.authorities)
        {
            as.add(new SimpleGrantedAuthority(s));
        }
        return as;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return 1000;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return 3000;
    }

    @Override
    public boolean isAutoApprove(String s) {
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setAuthorities (Set<String> as){
        this.authorities=as;
    }
}
