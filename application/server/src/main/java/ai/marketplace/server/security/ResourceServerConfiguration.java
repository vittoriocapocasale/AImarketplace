package ai.marketplace.server.security;

import ai.marketplace.server.authorities.AdminAuthority;
import ai.marketplace.server.authorities.UserAuthority;
import ai.marketplace.server.propertes.GeneralProperties;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
@Configuration
@EnableResourceServer
@EnableWebSecurity
@Order(3)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter
{


    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private DefaultTokenServices tokenServices;

    //server configured to protect resource RESOURCE_ID
    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config
                .resourceId(GeneralProperties.RESOURCE_ID)
                .tokenServices(tokenServices);
    }
    //defines which paths to secure
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                //.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class)
                .requiresChannel().antMatchers("/**").requiresSecure().and()
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .requestMatchers().antMatchers("/users/*","/users/*/**","/admins", "/admins/**","/clients", "/clients/**","/marketplace/**" ).and().authorizeRequests()
                .anyRequest().authenticated();
    }
}
