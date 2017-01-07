package se.omegapoint.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public void defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error("Request: " + req.toString() + "Error: " + e.getLocalizedMessage());
        throw e;
    }

}
