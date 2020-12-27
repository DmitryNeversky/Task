package org.magnit.task.controllers;

import org.magnit.task.entities.Notification;
import org.magnit.task.entities.User;
import org.magnit.task.repositories.NotificationRepository;
import org.magnit.task.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public UserController(UserRepository userRepository, NotificationRepository notificationRepository) {
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

    @GetMapping("/profile/edit")
    public String getEditUserPage(){

        return "profileEdit";
    }

    @PostMapping("/profile/edit")
    public String editUser(@ModelAttribute User user){
        userRepository.save(user);

        // Save new Image and delete old Image

        return "redirect:/profile/id" + user.getId();
    }

    @ModelAttribute
    public void getHeader(Principal principal, Model model){
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("userNotifies", user.getNotifications());
        model.addAttribute("currentUser", user);

        List<Notification> notifications = notificationRepository.findByLookAndUser(false, user);

        model.addAttribute("userNotifyCount", notifications.size());
    }
}
