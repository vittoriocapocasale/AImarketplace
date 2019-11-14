package ai.marketplace.server.authorities;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

public class UserAuthority implements GrantedAuthority
{
    public UserAuthority(){};
    @Override
    public String getAuthority()
    {
        return "USER";
    }
}
