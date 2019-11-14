package ai.marketplace.server.common;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//when thrown returns response status
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenException extends Exception {
}
