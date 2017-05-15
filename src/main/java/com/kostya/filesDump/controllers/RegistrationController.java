package com.kostya.filesDump.controllers;

import com.kostya.filesDump.entities.User;
import com.kostya.filesDump.repositories.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Костя on 14.05.2017.
 */
@Controller
@RequestMapping(value = "/register")
public class RegistrationController {
    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String getRegisterPageName(){
        return "register";
    }

    @PostMapping
    public String registerAndRedirect(@ModelAttribute("user")User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "/register";
        }

        if(userRepository.getUserByEmail(user.getEmail()) != null){
            bindingResult.rejectValue("email", "", "This email is already registered");
            return "/register";
        }

        user.addAuthority("ROLE_USER");
        userRepository.putUser(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userRepository.loadUserByUsername(user.getEmail()), user.getPassword(), user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        return "redirect:/";
    }
}
