package com.ReRollBag.exceptions.ExceptionHandler;

import com.ReRollBag.controller.BaseController;
import com.ReRollBag.enums.ErrorCode;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.exceptions.bagsExceptions.AlreadyRentedException;
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
        log.error("ReturnRequestUserMismatchException");
        ErrorJson errorJson = ErrorJson.builder()
                .message("ReturnRequestUserMismatchException")
                .errorCode(ErrorCode.ReturnRequestUserMismatchException.getErrorCode())
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyRentedException.class)
    public ResponseEntity<?> handleAlreadyRentedException(AlreadyRentedException e) {
        log.error("AlreadyRentedException");
        ErrorJson errorJson = ErrorJson.builder()
                .message("AlreadyRentedException")
                .errorCode(ErrorCode.AlreadyRentedException.getErrorCode())
                .build();
        return sendResponseHttpByJson(errorJson, HttpStatus.BAD_REQUEST);
    }

}
