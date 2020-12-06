package org.magnit.task.controllers;

import org.magnit.task.entities.*;
import org.magnit.task.repositories.IdeaRepository;
import org.magnit.task.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class IndexController {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;

    public IndexController(IdeaRepository ideaRepository, UserRepository userRepository) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String getIndexPage(Model model){

        List<Idea> ideas = ideaRepository.findAll();
        model.addAttribute("ideas", ideas);
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("ideaCount", ideaRepository.count());

        return "index";
    }

    @GetMapping("/idea-{idea}")
    public String getIdea(@PathVariable Idea idea, Model model){

        model.addAttribute("idea", idea);

        model.addAttribute("status", IdeaStatus.values());

        return "/idea";
    }

    @GetMapping("/new")
    public String getNewIdea(Model model){

        model.addAttribute("idea", new Idea());

        return "/new";
    }

    @PostMapping("/add")
    public String addIdea(@ModelAttribute Idea idea, Principal principal){

        User user = userRepository.findByUsername(principal.getName());

        idea.setUser(user);

        ideaRepository.save(idea);

        return "redirect:/";
    }

    @ModelAttribute
    public void getModel(Principal principal, Model model){
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", user);
    }

}
