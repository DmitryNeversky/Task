package org.magnit.task.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;
    private String description;

    @ManyToMany()
    @JoinTable(
            name = "idea_likes",
            joinColumns = { @JoinColumn(name = "idea_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> likes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private IdeaStatus status;

    @DateTimeFormat
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;

    public Idea(String title, String description, IdeaStatus status, Date date, User user) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.date = date;
        this.user = user;
    }

    @ElementCollection
    @CollectionTable(name = "idea_images_mapping",
            joinColumns = {@JoinColumn(name = "idea_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "image_name")
    @Column(name = "image_uuid")
    private Map<String, String> images = new HashMap<>();

    public void addImage(String key, String value){
        images.put(key, value);
    }

    @ElementCollection
    @CollectionTable(name = "idea_files_mapping",
            joinColumns = {@JoinColumn(name = "file_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "file_name")
    @Column(name = "file_uuid")
    private Map<String, String> files = new HashMap<>();

    public void addFile(String key, String value){
        files.put(key, value);
    }

    public void like(User user){
        boolean search = false;
        for(User pair : likes){
            if(pair.getId() == user.getId()) {
                search = true;
                break;
            }
        }

        if(search == true)
            likes.remove(user);
        else
            likes.add(user);
    }
}
