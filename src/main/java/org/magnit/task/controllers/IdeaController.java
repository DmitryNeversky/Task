package org.magnit.task.controllers;

import org.magnit.task.entities.*;
import org.magnit.task.repositories.IdeaRepository;
import org.magnit.task.repositories.NotificationRepository;
import org.magnit.task.repositories.UserRepository;
import org.magnit.task.services.IdeaService;
import org.magnit.task.services.MailSender;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("ideas")
public class IdeaController {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final MailSender mailSender;
    private final IdeaService ideaService;

    public IdeaController(IdeaRepository ideaRepository,
                          UserRepository userRepository,
                          NotificationRepository notificationRepository,
                          MailSender mailSender, IdeaService ideaService) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
        this.ideaService = ideaService;
    }

    @GetMapping
    public String getIdeas(
            @PageableDefault(
                    sort = {"id"},
                    direction = Sort.Direction.DESC,
                    size = 5) Pageable pageable,
            Model model){

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

    @GetMapping("/idea-{idea}/edit")
    public String getEdit(@PathVariable Idea idea, Model model){
        model.addAttribute("idea", idea);

        return "edit";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam int id,
            @RequestParam String title,
            @RequestParam String description
    ){

        Idea idea = ideaRepository.findById(id);
        idea.setTitle(title);
        idea.setDescription(description);

        ideaRepository.save(idea);

        return "redirect:/ideas/idea-" + id;
    }

    @PostMapping("/add")
    public String add(
            @RequestParam String title,
            @RequestParam String description,
            Principal principal
    ) {

        User user = userRepository.findByUsername(principal.getName());

        Idea idea = new Idea(title, description, IdeaStatus.LOOKING, new Date(), user);

        user.setIdeaCount(user.getIdeaCount() + 1);

        ideaRepository.save(idea);
        ideaRepository.flush();

        for(User pair : userRepository.findAllByRole(Roles.MODERATOR)){
            mailSender.send(
                    pair.getUsername(),
                    "Новая идея на портале Магнит IT для людей",
                    "Новая идея от " + idea.getUser().getName()
                            + ". Просмотреть идею: " + "http://localhost:8080/ideas/idea-" + idea.getId()
            );
        }

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

        try {
            mailSender.send(
                    idea.getUser().getUsername(),
                    "Статус идеи изменен",
                    "Статус вашей идеи " + idea.getTitle()
                            + " #" + idea.getId() + " изменен на " + idea.getStatus()
                            + ". Просмотреть идею: " + "http://localhost:8080/ideas/idea-" + idea.getId()
            );
        } catch (Exception e){
            e.printStackTrace();
        }

        return "redirect:idea-" + idea.getId();
    }

    @PostMapping("setLike-{idea}")
    public String setLike(@PathVariable Idea idea, Principal principal, @RequestParam boolean flag){
        User user = userRepository.findByUsername(principal.getName());

        if (flag) ideaService.addLike(user, idea);
        else ideaService.remLike(user, idea);

        ideaRepository.save(idea);
        ideaRepository.flush();

        return "redirect:";
    }

    @GetMapping("getIdeaBase")
    public ResponseEntity<InputStreamResource> getIdeaBase() {

        InputStreamResource resource = null;

        try { resource = ideaService.downloadIdeaBase(); }
        catch (IOException e) { e.printStackTrace(); }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.html")
                .body(resource);
    }

    @PostMapping
    @ResponseBody
    public Model doFilter(
            @RequestParam(required = false) IdeaStatus status,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String property,
            @RequestParam(required = false) String title,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, size = 5) Pageable pageable,
            Model model) {

        PageRequest pages;
        Page<Idea> ideas;

        if(status != IdeaStatus.ALL) {

            pages = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.fromString(direction), property)
            );

            if(title != null)
                ideas = ideaRepository.findByTitleContainingAndStatus(pages, title, status);
            else
                ideas = ideaRepository.findAllByStatus(pages, status);

        } else {

            pages = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.fromString(direction), property)
            );

            if(title == null)
                ideas = ideaRepository.findAll(pages);
            else
                ideas = ideaRepository.findAllByTitleContaining(pages, title);
        }

        model.addAttribute("ideas", ideas);
        model.addAttribute("pageable", pageable);

        return model;
    }

    @PostMapping("/remove-{idea}")
    public String remove(@PathVariable Idea idea){
        idea.getUser().setIdeaCount(idea.getUser().getIdeaCount() - 1);
        ideaRepository.delete(idea);

        return "redirect:/ideas";
    }

    @ModelAttribute
    public void getHeader(Principal principal, Model model){

        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("currentUser", user);

        if(user.getNotifications() != null)
            model.addAttribute("userNotifies", user.getNotifications());

        List<Notification> notifications = notificationRepository.findByLookAndUser(false, user);

        model.addAttribute("userNotifyCount", notifications.size());

        for(IdeaStatus pair : IdeaStatus.values()){
            model.addAttribute("status", pair);
        }
        for(Roles pair : Roles.values()){
            model.addAttribute("roles", pair);
        }

        model.addAttribute("statuses", IdeaStatus.values());
    }
}
