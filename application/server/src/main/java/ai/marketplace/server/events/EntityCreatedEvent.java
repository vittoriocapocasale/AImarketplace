package ai.marketplace.server.events;


import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpServletResponse;

//event thrown when entities are created
public class EntityCreatedEvent extends  ApplicationEvent{

    private HttpServletResponse response;
    private String resId;

    public EntityCreatedEvent(Object source, HttpServletResponse response, String resId) {
        super(source);
        this.resId=resId;
        this.response=response;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
    public String getResId() {
        return resId;
    }
}
