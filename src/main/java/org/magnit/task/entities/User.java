package org.magnit.task.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private @NotEmpty(message = "Поле не должно быть пустым") String name;

    private @NotEmpty(message = "Поле не должно быть пустым") String division;

    private @NotEmpty(message = "Поле не должно быть пустым") @Email String username;
    private @NotEmpty(message = "Поле не должно быть пустым") String password;

    private String avatarPath;

    private String phone;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date birthday;

    private String lang;
    private String about;

    private int ideaSize;
    private int ideaApprovedSize;
    private int ideaLookingSize;
    private int ideaDeniedSize;
    private int ideaCount;

    @Enumerated(EnumType.STRING)
    private Roles role;

    @OneToMany(mappedBy = "user")
    private List<Idea> ideas = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();
}