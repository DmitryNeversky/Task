package org.magnit.task.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

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
    private IdeaStatus ideaStatus;

}
