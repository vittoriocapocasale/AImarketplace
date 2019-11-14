package ai.marketplace.server.events;

import ai.marketplace.server.common.Functions;
import ai.marketplace.server.common.LinkUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;


//menages entity creation by adding headers to the response. Differently from response ready, adds also location header
@Component
public class EntityCreatedEventListener implements ApplicationListener<EntityCreatedEvent> {
    @Override
    public void onApplicationEvent(EntityCreatedEvent entityCreatedEvent) {

        HttpServletResponse response = entityCreatedEvent.getResponse();
        String resId=entityCreatedEvent.getResId();
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().
                path("/{resId}").buildAndExpand(resId).toUri();
        response.setHeader("Location", uri.toASCIIString());

        URI usersUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("users").build().toUri();
        String  usersLink= LinkUtil.createLinkHeader(usersUri.toASCIIString(), "users");
        response.addHeader("Link", usersLink);

        List<String> links=Functions.getAllHateoasLinks();
        for(String l:links)
        {
            response.addHeader("Link", l);
        }
    }
}
