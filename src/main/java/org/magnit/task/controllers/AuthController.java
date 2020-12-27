package org.magnit.task.controllers;

import org.magnit.task.entities.User;
import org.magnit.task.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String getRegistrationPage() {

        return "login";
    }

    @GetMapping("/registration")
    public String getRegistration(Model model){
        model.addAttribute("user", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String registration(Model model, @ModelAttribute @Valid User user, BindingResult bindingResult){

        if (bindingResult.hasErrors())
            return "registration";

        User userFromData = userRepository.findByUsername(user.getUsername());

        if(userFromData != null){
            model.addAttribute("error", "Аккаунт уже создан");
            return "registration";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAvatarPath("/static/images/user.png");

        userRepository.save(user);

        return "redirect:/";
    }
}
