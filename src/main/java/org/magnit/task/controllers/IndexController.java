package org.magnit.task.controllers;

import org.magnit.task.entities.*;
import org.magnit.task.repositories.IdeaRepository;
import org.magnit.task.repositories.NotificationRepository;
import org.magnit.task.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
public class IndexController {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public IndexController(IdeaRepository ideaRepository, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public String getIndexPage(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, size = 5) Pageable pageable, Model model){

        Page<Idea> ideas = ideaRepository.findAll(pageable);
        model.addAttribute("ideas", ideas);

        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("ideaCount", ideaRepository.count());

        model.addAttribute("pageable", pageable);

        return "index";
    }

    @PostMapping("/add")
    public String addIdea(@ModelAttribute Idea idea, Principal principal){

        User user = userRepository.findByUsername(principal.getName());

        idea.setUser(user);
        idea.setStatus(IdeaStatus.LOOKING);

        ideaRepository.save(idea);

        return "redirect:/#idea-" + idea.getId();
    }

    @PostMapping
    public String setIdeaStatus(@RequestParam int ideaID, @RequestParam("ideaStatus") IdeaStatus ideaStatus){

        Idea idea = ideaRepository.findById(ideaID);
        idea.setStatus(ideaStatus);

        ideaRepository.save(idea);

        return "redirect:/#idea-" + ideaID;
    }

    @PostMapping("/setNotifyLook")
    public void setNotifyLook(@RequestParam long id ){
        Notification notification = notificationRepository.findById(id);
        notification.setLook(true);
        notificationRepository.save(notification);
    }

    // Header panel
    @ModelAttribute
    public void getHeader(Principal principal, Model model){
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("userNotifies", user.getNotifications());
        model.addAttribute("user", user);

        List<Notification> notifications = notificationRepository.findByLook(false);

        model.addAttribute("userNotifyCount", notifications.size());
    }
}
