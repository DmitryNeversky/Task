package org.magnit.task.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private @NotEmpty(message = "Поле не должно быть пустым") String name;
    private @NotEmpty(message = "Поле не должно быть пустым") String serName;
    private @NotEmpty(message = "Поле не должно быть пустым") String secondName;

    private @NotEmpty(message = "Поле не должно быть пустым") String division;

    private @NotEmpty(message = "Поле не должно быть пустым") @Email String username;
    private @NotEmpty(message = "Поле не должно быть пустым") String password;

    private String avatarPath;

    @Enumerated(EnumType.STRING)
    private Roles role;

    @OneToMany(mappedBy = "user")
    @ElementCollection
    private List<Idea> ideas = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @ElementCollection
    private List<Notification> notifications = new ArrayList<>();

    public String getFullName(){
        return name + " " + serName + " " + secondName;
    }
}
