package org.magnit.task.controllers;

import org.magnit.task.entities.Roles;
import org.magnit.task.entities.User;
import org.magnit.task.repositories.UserRepository;
import org.magnit.task.services.UserDetailService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@Controller
public class IndexController {

    private final UserRepository userRepository;

    public IndexController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String getIndexPage(Principal principal, Model model){

        User user = userRepository.findByUsername(principal.getName());

        model.addAttribute("user", user);

        if (user.getRole() == Roles.MODERATOR)
            System.out.println("Moderator");

        return "index";
    }
}
