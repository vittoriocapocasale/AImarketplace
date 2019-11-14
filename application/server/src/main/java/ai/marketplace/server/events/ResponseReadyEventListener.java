package ai.marketplace.server.events;

import ai.marketplace.server.common.Functions;
import ai.marketplace.server.common.LinkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.Console;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


//handler of events thrown by any response. added links to response
@Component
public class ResponseReadyEventListener implements ApplicationListener<ResponseReadyEvent> {
    @Override
    public void onApplicationEvent(ResponseReadyEvent responseReadyEvent) {
        HttpServletResponse response = responseReadyEvent.getResponse();
        List<String> links=Functions.getAllHateoasLinks();
        for(String l:links)
        {
            response.addHeader("Link", l);
        }

    }
}
