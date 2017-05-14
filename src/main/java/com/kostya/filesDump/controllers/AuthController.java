package com.kostya.filesDump.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Костя on 13.05.2017.
 */

@Controller
public class AuthController {
    @RequestMapping("/auth")
    public String getAuthPageName(){
        return "auth";
    }
}
