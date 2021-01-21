package org.magnit.task.controllers;

import org.magnit.task.entities.IdeaStatus;
import org.magnit.task.entities.Notification;
import org.magnit.task.entities.User;
import org.magnit.task.repositories.NotificationRepository;
import org.magnit.task.repositories.UserRepository;
import org.magnit.task.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, NotificationRepository notificationRepository, UserService userService) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    @GetMapping("/profile/id{user}")
    public String getUserPage(@PathVariable User user, Model model){
        model.addAttribute("user", user);

        model.addAttribute("ideaCount", userService.ideasCount(user));
        model.addAttribute("approvedIdeas", userService.ideaStatusCount(user, IdeaStatus.APPROVED));
        model.addAttribute("lookingIdeas", userService.ideaStatusCount(user, IdeaStatus.LOOKING));
        model.addAttribute("deniedIdeas", userService.ideaStatusCount(user, IdeaStatus.DENIED));

        return "profile";
    }

    @GetMapping("/notify-{notification}")
    public String getNotify(@PathVariable Notification notification){
        notification.setLook(true);
        notificationRepository.save(notification);
        return "redirect:/ideas/idea-" + notification.getIdeaId();
    }

    @GetMapping("/profile/edit")
    public String getEditUserPage(Model model, Principal principal){
        model.addAttribute("user", userRepository.findByUsername(principal.getName()));

        return "profileEdit";
    }

    @PostMapping("/profile/edit-{id}")
    public String editUser(
            @PathVariable int id,
            @ModelAttribute User userModel,
            @RequestParam(required = false) MultipartFile avatar
            ) {

        User user = userRepository.findById(id);

        if(!userModel.getName().equals(user.getName()))
            user.setName(userModel.getName());
        if(!userModel.getDivision().equals(user.getDivision()))
            user.setDivision(userModel.getDivision());
        if(userModel.getBirthday() != user.getBirthday())
            user.setBirthday(userModel.getBirthday());
        if(!userModel.getPhone().equals(user.getPhone()))
            user.setPhone(userModel.getPhone());
        if(!userModel.getLang().equals(user.getLang()))
            user.setLang(userModel.getLang());
        if(!userModel.getAbout().equals(user.getAbout()))
            user.setAbout(userModel.getAbout());

        userService.uploadAvatar(avatar, user);

        userRepository.save(user);
        userRepository.flush();

        return "redirect:/profile/id" + id;
    }

    @ModelAttribute
    public void getHeader(Principal principal, Model model){

        User user = userRepository.findByUsername(principal.getName());

        if(user.getNotifications() != null)
            model.addAttribute("userNotifies", user.getNotifications());
        model.addAttribute("currentUser", user);

        List<Notification> notifications = notificationRepository.findByLookAndUser(false, user);
        model.addAttribute("userNotifyCount", notifications.size());
    }
}
