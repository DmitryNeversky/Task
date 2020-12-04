package org.magnit.task.controllers;

import org.magnit.task.entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("profile")
public class UserController {

    @GetMapping("/id{user}")
    public String getUserPage(@PathVariable User user, Model model){

        model.addAttribute("user", user);

        return "profile";
    }
}
