package org.magnit.task.controllers;

import org.magnit.task.entities.Idea;
import org.magnit.task.entities.User;
import org.magnit.task.repositories.IdeaRepository;
import org.magnit.task.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("idea")
public class IdeaController {

    private final UserRepository userRepository;
    private final IdeaRepository ideaRepository;

    public IdeaController(UserRepository userRepository, IdeaRepository ideaRepository) {
        this.userRepository = userRepository;
        this.ideaRepository = ideaRepository;
    }

    @GetMapping("/new")
    public String getNewIdea(Model model){

        model.addAttribute("idea", new Idea());

        return "idea";
    }

    @PostMapping("/add")
    public String addIdea(@ModelAttribute Idea idea, Principal principal){

        User user = userRepository.findByUsername(principal.getName());

        idea.setUser(user);

        ideaRepository.save(idea);

        return "redirect:/";
    }

}
