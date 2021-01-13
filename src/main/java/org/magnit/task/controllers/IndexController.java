package org.magnit.task.controllers;

import org.magnit.task.entities.Idea;
import org.magnit.task.entities.IdeaStatus;
import org.magnit.task.entities.Notification;
import org.magnit.task.entities.User;
import org.magnit.task.repositories.IdeaRepository;
import org.magnit.task.repositories.NotificationRepository;
import org.magnit.task.repositories.UserRepository;
import org.magnit.task.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationRepository notificationRepository;

    public IndexController(IdeaRepository ideaRepository, UserRepository userRepository, UserService userService, NotificationRepository notificationRepository) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public String getIndexPage(Model model){

        List<Idea> newIdeas = ideaRepository.findTop3ByOrderByIdDesc();
        model.addAttribute("newIdeas", newIdeas);

        List<Idea> topIdeas = ideaRepository.findTop3ByOrderByLikeCountDesc();
        model.addAttribute("topIdeas", topIdeas);

        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("ideaCount", ideaRepository.count());

        List<User> topUsers = userRepository.findTop3ByOrderByIdeaCountDesc();
        List<User> userList = new ArrayList<>();

        for(User pair : topUsers) {
            pair.setIdeaSize(userService.ideasCount(pair));
            pair.setIdeaApprovedSize(userService.ideaStatusCount(pair, IdeaStatus.APPROVED));
            pair.setIdeaLookingSize(userService.ideaStatusCount(pair, IdeaStatus.LOOKING));
            pair.setIdeaDeniedSize(userService.ideaStatusCount(pair, IdeaStatus.DENIED));
            userList.add(pair);
        }

        model.addAttribute("topUsers", userList);

        return "home";
    }

    @PostMapping("/add")
    public String addIdea(@ModelAttribute Idea idea, Principal principal){

        User user = userRepository.findByUsername(principal.getName());

        idea.setUser(user);
        idea.setStatus(IdeaStatus.LOOKING);

        ideaRepository.save(idea);

        return "redirect:/#idea-" + idea.getId();
    }

    @PostMapping("/setNotifyLook")
    public void setNotifyLook(@RequestParam long id ){
        Notification notification = notificationRepository.findById(id);
        notification.setLook(true);

        notificationRepository.save(notification);
    }

    @PostMapping("/removeNotify-{notify}")
    public String removeNotify(@PathVariable Notification notify, Model model, Principal principal){
        notificationRepository.delete(notify);

        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("userNotifies", user.getNotifications());
        model.addAttribute("currentUser", user);

        List<Notification> notifications = notificationRepository.findByLookAndUser(false, user);

        model.addAttribute("userNotifyCount", notifications.size());

        return "index";
    }

    // Header panel
    @ModelAttribute
    public void getHeader(Principal principal, Model model){
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("userNotifies", user.getNotifications());
        model.addAttribute("currentUser", user);

        List<Notification> notifications = notificationRepository.findByLookAndUser(false, user);

        model.addAttribute("userNotifyCount", notifications.size());

        for(IdeaStatus pair : IdeaStatus.values()){
            model.addAttribute("status", pair);
        }
    }
}
