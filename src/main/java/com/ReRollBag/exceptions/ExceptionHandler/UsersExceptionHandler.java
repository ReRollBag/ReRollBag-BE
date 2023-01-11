package com.ReRollBag.exceptions.ExceptionHandler;

import com.ReRollBag.controller.BaseController;
import com.ReRollBag.exceptions.CustomExceptions;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class UsersExceptionHandler extends BaseController {

    @ExceptionHandler(UsersIdAlreadyExistException.class)
    public ResponseEntity<?> handleDuplicatedUsersException (UsersIdAlreadyExistException e) {
        log.error("DuplicatedUsersException");
        ErrorJson errorJson = ErrorJson.builder()
                .message("UsersIdAlreadyExistException")
                .errorCode(CustomExceptions.UsersIdAlreadyExistException)
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.ACCEPTED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException (Exception e) {
        e.printStackTrace();
        log.error("Exception" + e.getMessage()) ;
        return sendResponseHttpByJson(null);
    }
}
