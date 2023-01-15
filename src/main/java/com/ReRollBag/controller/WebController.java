package com.ReRollBag.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Log4j2
@Controller
public class WebController {

    @GetMapping("docs/users")
    public String docs_users() {
        log.info("docs_users");
        return "users.html";
    }
}
