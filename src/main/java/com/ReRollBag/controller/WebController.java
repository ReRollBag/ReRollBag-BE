package com.ReRollBag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("docs/users")
    public String docs_users() {
        return "users.html";
    }

    @GetMapping("docs/auth")
    public String docs_auth() {
        return "auth.html";
    }

    @GetMapping("docs/bags")
    public String docs_bags() {
        return "bags.html";
    }

    @GetMapping("docs/markers")
    public String docs_markers() {
        return "markers.html";
    }

    @GetMapping("docs/notices")
    public String docs_notices() {
        return "notices.html";
    }

}
