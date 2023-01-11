package com.ReRollBag.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.nio.charset.Charset;

@ControllerAdvice
public class BaseController {
    public ResponseEntity<?> sendResponseHttpByJson(Object data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<Object> (data, headers, HttpStatus.OK);
    }

    public ResponseEntity<?>sendResponseHttpByJson (Object data, HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<Object> (data, headers, httpStatus);
    }

}
