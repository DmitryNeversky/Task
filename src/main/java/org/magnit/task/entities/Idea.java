package org.magnit.task.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Min(value = 5, message = "Заголовок слишком короткий (минимум 5 символов)")
    private String title;

    @Min(value = 55, message = "Описание слишком короткое (минимум 55 символов).")
    private String description;

    private long likeCount;

    @ManyToMany()
    @JoinTable(
            name = "idea_likes",
            joinColumns = { @JoinColumn(name = "idea_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> likes = new HashSet<>();

    @ManyToMany()
    @JoinTable(
            name = "idea_unlikes",
            joinColumns = { @JoinColumn(name = "idea_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> unLikes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private IdeaStatus status;

    @Basic
    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
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

    public void addLike(User user) {
        boolean searchLikes = false;
        for (User pair : likes) {
            if (pair.getId() == user.getId()) {
                searchLikes = true;
                break;
            }
        }
        boolean searchUnLikes = false;
        for (User pair : unLikes) {
            if (pair.getId() == user.getId()) {
                searchUnLikes = true;
                break;
            }
        }

        if (searchUnLikes){
            setLikeCount(getLikeCount() + 1);
            unLikes.remove(user);
        }

        if (!searchLikes) {
            setLikeCount(getLikeCount() + 1);
            likes.add(user);
        } else {
            setLikeCount(getLikeCount() - 1);
            likes.remove(user);
        }
    }

    public void remLike(User user) {
        boolean searchUnLikes = false;
        for (User pair : unLikes) {
            if (pair.getId() == user.getId()) {
                searchUnLikes = true;
                break;
            }
        }
        boolean searchLikes = false;
        for (User pair : likes) {
            if (pair.getId() == user.getId()) {
                searchLikes = true;
                break;
            }
        }

        if (searchLikes){
            setLikeCount(getLikeCount() - 1);
            likes.remove(user);
        }

        if (!searchUnLikes) {
            setLikeCount(getLikeCount() - 1);
            unLikes.add(user);
        } else {
            setLikeCount(getLikeCount() + 1);
            unLikes.remove(user);
        }
    }
}
