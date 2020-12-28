package org.magnit.task.controllers;

import org.magnit.task.entities.Notification;
import org.magnit.task.entities.User;
import org.magnit.task.repositories.NotificationRepository;
import org.magnit.task.repositories.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    public String editUser(
            @RequestParam(required = false) MultipartFile avatar,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String division,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date birthday,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String about,
            Principal principal
            ){

        User user = userRepository.findByUsername(principal.getName());

        user.setName(name);
        user.setDivision(division);
        user.setBirthday(birthday);
        user.setPhone(phone);
        user.setLang(lang);
        user.setAbout(about);

        // Save new Image and delete old Image

        if(!Objects.requireNonNull(avatar.getOriginalFilename()).isEmpty()) {

            System.out.println("NON");

            String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(avatar.getOriginalFilename()));

            try {
                Path path = Paths.get("/home/koshey/Документы/task/src/main/resources/uploads/avatar/" + fileName);
                Files.copy(avatar.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                user.setAvatarPath("/uploads/avatar/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
