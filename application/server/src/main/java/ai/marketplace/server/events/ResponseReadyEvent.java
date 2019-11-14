package ai.marketplace.server.events;

import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpServletResponse;


//Generic event thrown by any ready response
public class ResponseReadyEvent extends ApplicationEvent {

    private HttpServletResponse response;
    public ResponseReadyEvent(Object source, HttpServletResponse response) {
        super(source);
        this.response=response;

    }
    public HttpServletResponse getResponse() {
        return response;
    }
}
