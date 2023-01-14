package com.ReRollBag.exceptions.ExceptionHandler;

import com.ReRollBag.controller.BaseController;
import com.ReRollBag.exceptions.ErrorCode;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdOrPasswordInvalidException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
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
                .errorCode(ErrorCode.UsersIdAlreadyExistException.getErrorCode())
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.ACCEPTED);
    }

    @ExceptionHandler(UsersIdOrPasswordInvalidException.class)
    public ResponseEntity<?> handleUsersIdOrPasswordInvalidException(UsersIdOrPasswordInvalidException e) {
        log.error("UsersIdOrPasswordInvalidException");
        ErrorJson errorJson = ErrorJson.builder()
                .message("UsersIdOrPasswordInvalidException")
                .errorCode(ErrorCode.UsersIdOrPasswordInvalidException.getErrorCode())
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.ACCEPTED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException e) {
        log.error("SignatureException");
        ErrorJson errorJson = ErrorJson.builder()
                .message("SignatureException")
                .errorCode(ErrorCode.SignatureException.getErrorCode())
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException(SignatureException e) {
        log.error("SignatureException");
        ErrorJson errorJson = ErrorJson.builder()
                .message("SignatureException")
                .errorCode(ErrorCode.SignatureException.getErrorCode())
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException e) {
        log.error("ExpiredJwtException");
        ErrorJson errorJson = ErrorJson.builder()
                .message("ExpiredJwtException")
                .errorCode(ErrorCode.ExpiredJwtException.getErrorCode())
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.ACCEPTED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        log.error("Exception" + e.getMessage());
        return sendResponseHttpByJson(null);
    }
}
