package org.magnit.task.controllers;

import org.magnit.task.entities.*;
import org.magnit.task.repositories.IdeaRepository;
import org.magnit.task.repositories.NotificationRepository;
import org.magnit.task.repositories.UserRepository;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
@RequestMapping("ideas")
public class IdeaController {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final MailSender mailSender;

    public IdeaController(IdeaRepository ideaRepository,
                          UserRepository userRepository,
                          NotificationRepository notificationRepository,
                          MailSender mailSender) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
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

    @GetMapping("/idea-{idea}/edit")
    public String getEdit(@PathVariable Idea idea, Model model){
        model.addAttribute("idea", idea);

        return "edit";
    }

    @PostMapping("/save")
    public String save(@RequestParam int id, @RequestParam String title, @RequestParam String description){

        Idea idea = ideaRepository.findById(id);
        idea.setTitle(title);
        idea.setDescription(description);

        ideaRepository.save(idea);

        return "redirect:/ideas/idea-" + id;
    }

    // It need for optimization

    private final String UPLOAD_IMAGE_DIR = "/home/koshey/Документы/task/src/main/resources/uploads/images/";
    private final String UPLOAD_FILE_DIR = "/home/koshey/Документы/task/src/main/resources/uploads/files/";

    @PostMapping("/add")
    public String add(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam(required = false) List<MultipartFile> files,
            Principal principal
    ) {

        User user = userRepository.findByUsername(principal.getName());

        Idea idea = new Idea(title, description, IdeaStatus.LOOKING, new Date(), user);

        // Upload Images

//        for(MultipartFile pair : images) {
//            if (Objects.requireNonNull(pair.getOriginalFilename()).isEmpty())
//                continue;
//
//            // normalize the file path
//            String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(pair.getOriginalFilename()));
//
//            // save the file on the local file system
//            try {
//                Path path = Paths.get(UPLOAD_IMAGE_DIR + fileName);
//                Files.copy(pair.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//                idea.addImage(pair.getOriginalFilename(), fileName);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // Upload Files
//
//        for(MultipartFile pair : files) {
//            if (Objects.requireNonNull(pair.getOriginalFilename()).isEmpty())
//                continue;
//
//            // normalize the file path
//            String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(pair.getOriginalFilename()));
//
//            // save the file on the local file system
//            try {
//                Path path = Paths.get(UPLOAD_FILE_DIR + fileName);
//                Files.copy(pair.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//                idea.addFile(pair.getOriginalFilename(), fileName);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        user.setIdeaCount(user.getIdeaCount() + 1);

        // Find moderators and send the message

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
        IdeaStatus ideaStatus = IdeaStatus.getValueByName(status);
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
                            + " #" + idea.getId() + " изменен на " + ideaStatus.getName()
                            + ". Просмотреть идею: " + "http://localhost:8080/ideas/idea-" + idea.getId()
            );
        } catch (Exception e){
            e.printStackTrace();
        }

        return "redirect:idea-" + idea.getId();
    }

    @PostMapping("setLike-{idea}")
    public String setLike(@PathVariable Idea idea, Principal principal, Model model, @RequestParam boolean flag){
        User user = userRepository.findByUsername(principal.getName());

        if (flag){
            idea.addLike(user);
        } else {
            idea.remLike(user);
        }

        ideaRepository.save(idea);
        ideaRepository.flush();

        return "redirect:";
    }

    private final String DATA_FILE = "/home/koshey/Документы/task/src/main/resources/uploads/files/data.html";

    @GetMapping("getIdeasData")
    public ResponseEntity<InputStreamResource> getIdeasData(String param) throws IOException {

        if(!(new File(String.valueOf(Paths.get(DATA_FILE))).exists()))
            Files.createFile(Paths.get(DATA_FILE));

        FileWriter fw = new FileWriter(DATA_FILE);

        StringBuilder str = new StringBuilder();

        str.append("<!DOCTYPE html>" + "<html lang='en'>" + "<head>" + "<meta charset='UTF-8'>" + "<title>Title</title>" + "</head>" + "<body>" + "<h1>Реестр идей</h1>");

        for(Idea pair : ideaRepository.findAll()){
            str.append("\n<p><b><u>Номер: ").append(pair.getId()).append("</u></b></p>").append("<p><b>Заголовок: </b><i>").append(pair.getTitle()).append("</i></p>").append("<p><b>Описание: </b><i>").append(pair.getDescription()).append("</i></p>").append("<p>Статус: <i>").append(pair.getStatus().getName()).append("</i></p>").append("<p>Рейтинг: <i>").append(pair.getLikeCount()).append("</i></p>").append("<p>Создано: <i>").append(pair.getDate()).append("</i></p>").append("<p>Автор: <i>").append(pair.getUser().getName()).append("</i></p>").append("<p>Подразделение: <i>").append(pair.getUser().getDivision()).append("</i></p><hr>");
        }

        str.append("</body>" + "</html>");

        fw.write(String.valueOf(str));
        fw.close();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(String.valueOf(Paths.get(DATA_FILE))));

        Files.delete(Paths.get(DATA_FILE));

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
