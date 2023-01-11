package com.ReRollBag.controller;

import com.ReRollBag.domain.dto.UsersLoginRequestDto;
import com.ReRollBag.domain.dto.UsersSaveRequestDto;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UsersController extends BaseController {
    private final UsersService usersService;

    @PostMapping("api/users/save")
    public ResponseEntity<?> save (@RequestBody UsersSaveRequestDto requestDto) {
        return sendResponseHttpByJson(usersService.save(requestDto));
    }

    @PostMapping("api/users/login")
    public ResponseEntity<?> login (@RequestBody UsersLoginRequestDto requestDto) {
        return sendResponseHttpByJson(usersService.login(requestDto));
    }

    @GetMapping("api/users/checkUserExist/{usersId}")
    public ResponseEntity<?> checkUserExist (@PathVariable String usersId) throws UsersIdAlreadyExistException {
        return sendResponseHttpByJson(usersService.checkUserExist(usersId));
    }
}
