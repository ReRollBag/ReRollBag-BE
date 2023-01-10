package com.ReRollBag.controller;

import com.ReRollBag.domain.dto.UsersSaveRequestDto;
import com.ReRollBag.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UsersController extends BaseController {
    private final UsersService usersService;

    @PostMapping("api/users/save")
    public ResponseEntity<?> save (@RequestBody UsersSaveRequestDto requestDto) {
        return sendResponseHttpByJson(usersService.save(requestDto));
    }
}
