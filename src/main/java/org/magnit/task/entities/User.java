package org.magnit.task.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private String serName;
    private String secondName;

    private String division;

    private String email;
    private String password;
    private String avatarPath;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    @ElementCollection
    private List<Idea> ideas = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @ElementCollection
    private List<Notification> notifications = new ArrayList<>();

}
