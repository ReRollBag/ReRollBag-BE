package com.ReRollBag.exceptions.ExceptionHandler;

import com.ReRollBag.controller.BaseController;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.exceptions.adminExceptions.CertificationSignatureException;
import com.ReRollBag.exceptions.adminExceptions.CertificationTimeExpireException;
import com.ReRollBag.exceptions.adminExceptions.UsersIsAlreadyAdminException;
import com.ReRollBag.exceptions.adminExceptions.UsersIsNotAdminException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class AdminExceptionHandler extends BaseController {

    @ExceptionHandler(UsersIsAlreadyAdminException.class)
    public ResponseEntity<?> handleUsersIsAlreadyAdminException(UsersIsAlreadyAdminException e) {
        log.error("UsersIsAlreadyAdminException");
        String errorMessages = "Users is Already Admin";
        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(6000)
                .message(errorMessages)
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsersIsNotAdminException.class)
    public ResponseEntity<?> handleUsersIsNotAdminException(UsersIsNotAdminException e) {
        log.error("UsersIsNotAdminException");
        String errorMessages = "Users is not Admin";
        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(6001)
                .message(errorMessages)
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CertificationSignatureException.class)
    public ResponseEntity<?> handleCertificationSignatureException(CertificationSignatureException e) {
        log.error("CertificationSignatureException");
        String errorMessages = "Certification Number is wrong";
        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(6002)
                .message(errorMessages)
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CertificationTimeExpireException.class)
    public ResponseEntity<?> handleCertificationSignatureException(CertificationTimeExpireException e) {
        log.error("CertificationTimeExpireException");
        String errorMessages = "Certification Number is expired";
        ErrorJson errorJson = ErrorJson.builder()
                .errorCode(6003)
                .message(errorMessages)
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.FORBIDDEN);
    }


}
