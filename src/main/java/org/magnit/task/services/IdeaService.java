package org.magnit.task.services;

import org.magnit.task.entities.Idea;
import org.magnit.task.entities.User;
import org.magnit.task.repositories.IdeaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
public class IdeaService {

    @Value("${upload.images.path}")
    private String IMAGES_PATH;

    @Value("${upload.files.path}")
    private String FILES_PATH;

    private final IdeaRepository ideaRepository;

    public IdeaService(IdeaRepository ideaRepository) {
        this.ideaRepository = ideaRepository;
    }

    public InputStreamResource downloadIdeaBase() throws IOException {

        if(!(new File(String.valueOf(Paths.get(FILES_PATH + "data.html"))).exists()))
            Files.createFile(Paths.get(FILES_PATH + "data.html"));

        FileWriter fw = new FileWriter(FILES_PATH + "data.html");

        StringBuilder str = new StringBuilder();

        str.append("<!DOCTYPE html>" + "<html lang='en'>" + "<head>" + "<meta charset='UTF-8'>" + "<title>Title</title>" + "</head>" + "<body>" + "<h1>Реестр идей</h1>");

        for(Idea pair : ideaRepository.findAll()){
            str.append("\n<p><b><u>Номер: ")
                    .append(pair.getId())
                    .append("</u></b></p>")
                    .append("<p><b>Заголовок: </b><i>")
                    .append(pair.getTitle())
                    .append("</i></p>")
                    .append("<p><b>Описание: </b><i>")
                    .append(pair.getDescription())
                    .append("</i></p>")
                    .append("<p>Статус: <i>")
                    .append(pair.getStatus().getName())
                    .append("</i></p>")
                    .append("<p>Рейтинг: <i>")
                    .append(pair.getLikeCount())
                    .append("</i></p>")
                    .append("<p>Создано: <i>")
                    .append(pair.getDate())
                    .append("</i></p>")
                    .append("<p>Автор: <i>")
                    .append(pair.getUser().getName())
                    .append("</i></p>")
                    .append("<p>Подразделение: <i>")
                    .append(pair.getUser().getDivision())
                    .append("</i></p><hr>");
        }

        str.append("</body>" + "</html>");

        fw.write(String.valueOf(str));
        fw.close();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(String.valueOf(Paths.get(FILES_PATH + "data.html"))));

        Files.delete(Paths.get(FILES_PATH + "data.html"));

        return resource;
    }

    public void uploadImages(List<MultipartFile> images, Idea idea){

        if(images != null) {
            for (MultipartFile pair : images) {
                if (Objects.requireNonNull(pair.getOriginalFilename()).isEmpty())
                    continue;

                String fileName = java.util.UUID.randomUUID() + "_"
                        + StringUtils.cleanPath(Objects.requireNonNull(pair.getOriginalFilename()));

                try {
                    Path path = Paths.get(IMAGES_PATH + fileName);
                    Files.copy(pair.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    idea.addImage(pair.getOriginalFilename(), fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void uploadFiles(List<MultipartFile> files, Idea idea){

        if(files != null) {
            for (MultipartFile pair : files) {
                if (Objects.requireNonNull(pair.getOriginalFilename()).isEmpty())
                    continue;

                String fileName = java.util.UUID.randomUUID() + "_"
                        + StringUtils.cleanPath(Objects.requireNonNull(pair.getOriginalFilename()));

                try {
                    Path path = Paths.get(FILES_PATH + fileName);
                    Files.copy(pair.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    addFile(pair.getOriginalFilename(), fileName, idea);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addFile(String key, String value, Idea idea){
        idea.getFiles().put(key, value);
    }

    public void addLike(User user, Idea idea) {
        boolean searchLikes = false;
        for (User pair : idea.getLikes()) {
            if (pair.getId() == user.getId()) {
                searchLikes = true;
                break;
            }
        }
        boolean searchUnLikes = false;
        for (User pair : idea.getUnLikes()) {
            if (pair.getId() == user.getId()) {
                searchUnLikes = true;
                break;
            }
        }

        if (searchUnLikes){
            idea.setLikeCount(idea.getLikeCount() + 1);
            idea.getUnLikes().remove(user);
        }

        if (!searchLikes) {
            idea.setLikeCount(idea.getLikeCount() + 1);
            idea.getLikes().add(user);
        } else {
            idea.setLikeCount(idea.getLikeCount() - 1);
            idea.getLikes().remove(user);
        }
    }

    public void remLike(User user, Idea idea) {
        boolean searchUnLikes = false;
        for (User pair : idea.getUnLikes()) {
            if (pair.getId() == user.getId()) {
                searchUnLikes = true;
                break;
            }
        }
        boolean searchLikes = false;
        for (User pair : idea.getLikes()) {
            if (pair.getId() == user.getId()) {
                searchLikes = true;
                break;
            }
        }

        if (searchLikes){
            idea.setLikeCount(idea.getLikeCount() - 1);
            idea.getLikes().remove(user);
        }

        if (!searchUnLikes) {
            idea.setLikeCount(idea.getLikeCount() - 1);
            idea.getUnLikes().add(user);
        } else {
            idea.setLikeCount(idea.getLikeCount() + 1);
            idea.getUnLikes().remove(user);
        }
    }
}
