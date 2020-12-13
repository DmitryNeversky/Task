package org.magnit.task.controllers;

import org.magnit.task.entities.*;
import org.magnit.task.repositories.IdeaRepository;
import org.magnit.task.repositories.NotificationRepository;
import org.magnit.task.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("ideas")
public class IdeaController {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public IdeaController(IdeaRepository ideaRepository, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public String getIdeas(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, size = 5) Pageable pageable, Model model){

        Page<Idea> ideas = ideaRepository.findAll(pageable);
        model.addAttribute("ideas", ideas);
        model.addAttribute("pageable", pageable);

        return "ideas";
    }

    @GetMapping("/idea-{idea}")
    public String getIdea(@PathVariable Idea idea, Model model){
        model.addAttribute("idea", idea);

        return "idea";
    }

    @GetMapping("/new")
    public String getNew(Model model){
        model.addAttribute("idea", new Idea());
        return "new";
    }

    // It need for optimization

    private final String UPLOAD_IMAGE_DIR = "C:/Users/Koshey/IdeaProjects/task/src/main/resources/uploads/images/";
    private final String UPLOAD_FILE_DIR = "C:/Users/Koshey/IdeaProjects/task/src/main/resources/uploads/files/";

    @PostMapping("/add")
    public String uploadFile(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam(required = false) List<MultipartFile> files,
            Principal principal
    ) {

        User user = userRepository.findByUsername(principal.getName());

        Idea idea = new Idea(title, description, IdeaStatus.LOOKING, new Date(), user);

        // Upload Images

        for(MultipartFile pair : images) {
            if (Objects.requireNonNull(pair.getOriginalFilename()).isEmpty())
                continue;

            // normalize the file path
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(pair.getOriginalFilename()));

            // save the file on the local file system
            try {
                Path path = Paths.get(UPLOAD_IMAGE_DIR + UUID.randomUUID() + "_" + fileName);
                Files.copy(pair.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                idea.addImage(path.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Upload Files

        for(MultipartFile pair : files) {
            if (Objects.requireNonNull(pair.getOriginalFilename()).isEmpty())
                continue;

            // normalize the file path
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(pair.getOriginalFilename()));

            // save the file on the local file system
            try {
                Path path = Paths.get(UPLOAD_FILE_DIR + UUID.randomUUID() + "_" + fileName);
                Files.copy(pair.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                idea.addFile(path.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ideaRepository.save(idea);

        return "redirect:/";
    }

    @PostMapping("setStatus-{idea}")
    public String setStatus(@PathVariable Idea idea, @RequestParam String status){
        idea.setStatus(IdeaStatus.getValueByName(status));
        ideaRepository.save(idea);

        User user = idea.getUser();

        Notification notification = new Notification(
                "Изменение статуса",
                "Статус вашей идеи с заголовком " + idea.getTitle() + " был изменен.",
                user, idea.getId());

        notificationRepository.save(notification);

        return "redirect:idea-" + idea.getId();
    }

    @ModelAttribute
    public void getHeader(Principal principal, Model model){

        User user = userRepository.findByUsername(principal.getName());

        model.addAttribute("userNotifies", user.getNotifications());

        if (user.getRole() == Roles.USER)
            model.addAttribute("user", user);
        else
            model.addAttribute("admin", user);

        List<Notification> notifications = notificationRepository.findByLook(false);

        model.addAttribute("userNotifyCount", notifications.size());

        for(IdeaStatus pair : IdeaStatus.values()){
            model.addAttribute("status", pair);
        }
    }
}
