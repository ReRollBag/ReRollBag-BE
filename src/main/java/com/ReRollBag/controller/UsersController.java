package com.ReRollBag.controller;

import com.ReRollBag.domain.dto.UsersLoginRequestDto;
import com.ReRollBag.domain.dto.UsersSaveRequestDto;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdOrPasswordInvalidException;
import com.ReRollBag.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UsersController extends BaseController {
    private final UsersService usersService;

    @PostMapping("api/v2/users/save")
    public ResponseEntity<?> save (@RequestBody UsersSaveRequestDto requestDto) {
        return sendResponseHttpByJson(usersService.save(requestDto));
    }

    @PostMapping("api/v2/users/login")
    public ResponseEntity<?> login (@RequestBody UsersLoginRequestDto requestDto) throws UsersIdOrPasswordInvalidException {
        return sendResponseHttpByJson(usersService.login(requestDto));
    }

    @GetMapping("api/v2/users/checkUserExist/{usersId}")
    public ResponseEntity<?> checkUserExist (@PathVariable String usersId) throws UsersIdAlreadyExistException {
        return sendResponseHttpByJson(usersService.checkUserExist(usersId));
    }

    @GetMapping("api/v1/users/dummyMethod")
    public ResponseEntity<?> dummyMethod () {
        return sendResponseHttpByJson(usersService.dummyMethod());
    }
}