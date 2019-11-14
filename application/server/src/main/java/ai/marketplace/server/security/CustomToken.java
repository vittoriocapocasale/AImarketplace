package ai.marketplace.server.security;

import ai.marketplace.server.structures.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;


//Custom jwt token with additional info
public class CustomToken implements TokenEnhancer
{

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();
        User user=(User)oAuth2Authentication.getPrincipal();
        String role;
        String username;
        if(user!=null&&user.getAuthorities()!=null&&user.getAuthorities().size()>0)
        {
            role=user.getAuthorities().iterator().next().getAuthority();
            username=user.getUsername();
        }
        else
        {
            role="";
            username="";
        }
        additionalInfo.put("role",role);
        additionalInfo.put("username",username);
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
        return  oAuth2AccessToken;
    }
}
