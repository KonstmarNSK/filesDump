package com.kostya.filesDump.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;

/**
 * Created by Костя on 13.05.2017.
 */
@Controller
public class FilesListController {
    @Autowired
    ServletContext servletContext;

    @RequestMapping("/")
    public String getResponse(Model model){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("password", userDetails.getPassword());
        model.addAttribute("email", userDetails.getUsername());
        model.addAttribute("contextPath", servletContext.getContextPath());
        return "viewFiles";
    }
}
