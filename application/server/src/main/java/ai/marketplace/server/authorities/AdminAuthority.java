package ai.marketplace.server.authorities;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

public class AdminAuthority implements GrantedAuthority
{
    public AdminAuthority(){};
    @Override
    public String getAuthority() {
        return "ADMIN";
    }
}
