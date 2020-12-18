package org.magnit.task.controllers;

import org.magnit.task.entities.Notification;
import org.magnit.task.entities.User;
import org.magnit.task.repositories.IdeaRepository;
import org.magnit.task.repositories.NotificationRepository;
import org.magnit.task.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public UserController(IdeaRepository ideaRepository, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/profile/id{user}")
    public String getUserPage(@PathVariable User user, Model model){
        model.addAttribute("user", user);

        return "profile";
    }

    @GetMapping("/notify-{notification}")
    public String getNotify(@PathVariable Notification notification){
        notification.setLook(true);
        notificationRepository.save(notification);
        return "redirect:/ideas/idea-" + notification.getIdeaId();
    }

//    @GetMapping("/profile/id{user}/edit")
//    public String getUserPage(@PathVariable User user, Model model){
//        model.addAttribute("user", user);
//
//        return "profileEdit";
//    }

}
