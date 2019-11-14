package ai.marketplace.server.common;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


//class for logging messages
@Component
public class FileLogger {
    public final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
}
