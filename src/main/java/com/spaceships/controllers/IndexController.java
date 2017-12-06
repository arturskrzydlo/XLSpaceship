package com.spaceships.controllers;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jt on 11/6/15.
 */

public class IndexController {

    @RequestMapping({"/"})
    public String index() {
        return "redirect:/login";
    }

    @RequestMapping("/access_denied")
    public String notAuth() {
        return "access_denied.html";
    }

    @RequestMapping({"/login"})
    public String login() {
        return "redirect:/login.html";
    }
}
