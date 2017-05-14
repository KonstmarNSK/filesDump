package com.kostya.filesDump.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Костя on 13.05.2017.
 */
@Controller
public class HomeController {
    @RequestMapping("/")
    public String getResponse(Model model){
        model.addAttribute("tmpValue", "MeFromModel!");
        return "home";
    }
}
