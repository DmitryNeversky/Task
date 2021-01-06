package org.magnit.task.controllers;

import org.magnit.task.entities.Notification;
import org.magnit.task.entities.User;
import org.magnit.task.repositories.NotificationRepository;
import org.magnit.task.repositories.UserRepository;
import org.magnit.task.services.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public String editUser(
            @RequestParam(required = false) MultipartFile avatar,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String division,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date birthday,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String about,
            Principal principal
            ) {

        User user = userRepository.findByUsername(principal.getName());

        user.setName(name);
        user.setDivision(division);
        user.setBirthday(birthday);
        user.setPhone(phone);
        user.setLang(lang);
        user.setAbout(about);

        System.out.println(birthday);

        userService.uploadAvatar(avatar, user);

        userRepository.save(user);
        userRepository.flush();

        return "redirect:/profile/id" + user.getId();
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
