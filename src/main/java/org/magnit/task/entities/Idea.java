package org.magnit.task.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;
    private String description;
    private long likes;

    @Enumerated(EnumType.STRING)
    private IdeaStatus status;

    @DateTimeFormat
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;

    @ElementCollection
    @Column(name = "image_name")
    @JoinColumn(name = "idea_id")
    private List<String> imageNames = new ArrayList<>();

    @ElementCollection
    @Column(name = "file_name")
    @JoinColumn(name = "idea_id")
    private List<String> fileNames = new ArrayList<>();

    public void addImage(String path){
        imageNames.add(path);
    }

    public void addFile(String path){
        fileNames.add(path);
    }

    public Idea(String title, String description, IdeaStatus status, Date date, User user) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.date = date;
        this.user = user;
    }
}
