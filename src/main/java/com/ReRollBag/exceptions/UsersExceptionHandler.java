package com.ReRollBag.exceptions;

import com.ReRollBag.controller.BaseController;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class UsersExceptionHandler extends BaseController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException (Exception e) {
        log.error("Exception!");
        return sendResponseHttpByJson(null);
    }

}
