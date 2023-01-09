package com.ReRollBag.controller;

import com.ReRollBag.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UsersController extends BaseController {
    private final UsersService usersService;

}
