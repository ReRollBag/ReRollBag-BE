package com.ReRollBag.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.Charset;

public class BaseController {
    public ResponseEntity<Object> sendResponseHttpByJson(Object data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<Object> (data, headers, HttpStatus.OK);
    }
}
