package com.ReRollBag.exceptions.ExceptionHandler;

import com.ReRollBag.controller.BaseController;
import com.ReRollBag.enums.ErrorCode;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.exceptions.bagsExceptions.ReturnRequestUserMismatchException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class BagsExceptionHandler extends BaseController {

    @ExceptionHandler(ReturnRequestUserMismatchException.class)
    public ResponseEntity<?> handleBagsExceptionHandler(ReturnRequestUserMismatchException e) {
        log.error("ReturnRequestUserMismachException");
        ErrorJson errorJson = ErrorJson.builder()
                .message("ReturnRequestUserMismachException")
                .errorCode(ErrorCode.ReturnRequestUserMismatchException.getErrorCode())
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.BAD_REQUEST);
    }

}
