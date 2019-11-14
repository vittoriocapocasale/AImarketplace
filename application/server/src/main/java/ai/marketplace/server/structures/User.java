package ai.marketplace.server.structures;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

//implementation of UserDetails, for automatic identity check

@Document(collection = "users")
public class User implements UserDetails
{
    private static Duration ValidityPeriod= Duration.ofDays(365*5);
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String username;
    private String password;
    private Boolean accountNonLocked;
    private Boolean credentialNonExpired;
    private Boolean enabled;
    private Instant from;
    private Set<String> authorities;
    private ArrayList<SubdocumentArchive> boughtArchives;
    private Long credit;

    public User()
    {
        this.id=new ObjectId();
        username="";
        password="";
        accountNonLocked=true;
        credentialNonExpired=true;
        enabled=true;
        from=Calendar.getInstance().toInstant();
        this.boughtArchives=new ArrayList<>();
        this.credit=0L;

    };

    public User(String username, String password, Boolean nonLocked, Boolean credentialNonExpired, Boolean enabled, Set<String> authorities)
    {
        this.id=new ObjectId();
        this.username = username;
        this.password = password;
        this.accountNonLocked = nonLocked;
        this.credentialNonExpired = credentialNonExpired;
        this.enabled = enabled;
        this.authorities=authorities;
        from=Calendar.getInstance().toInstant();
        this.boughtArchives=new ArrayList<>();
        this.credit=0L;
    }

    public User (String username, String password)
    {
        this.id=new ObjectId();
        this.username=username;
        this.password=password;
        accountNonLocked=true;
        credentialNonExpired=true;
        enabled=true;
        from=Calendar.getInstance().toInstant();
        this.boughtArchives=new ArrayList<>();
        this.credit=0L;
    }


    public ObjectId getId() {
        return id;
    }

    public ArrayList<SubdocumentArchive> getBoughtArchives() {
        return boughtArchives;
    }

    public Long getCredit()
    {
        return this.credit;
    }

    public void setCredit(long credit)
    {
        this.credit=credit;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> as=new ArrayList<>();
        for(String a:this.authorities)
        {
            as.add(new SimpleGrantedAuthority(a));
        }
        return as;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return Duration.between(Calendar.getInstance().toInstant(),this.from).compareTo(User.ValidityPeriod)<0;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialNonExpired(Boolean credentialNonExpired) {
        this.credentialNonExpired = credentialNonExpired;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString()
    {
        return  this.username+" "+this.password+" "+this.id+" "+this.getAuthorities().size();
    }
}
