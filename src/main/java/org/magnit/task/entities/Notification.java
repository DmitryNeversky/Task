package org.magnit.task.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;
    private String message;
    private long ideaId;

    @Basic
    @Temporal(TemporalType.DATE)
    private Date date;

    private boolean look;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private User user;

    public Notification(){

    }

    public Notification(String title, String message, User user, long ideaId) {
        this.title = title;
        this.message = message;
        this.user = user;
        this.ideaId = ideaId;
    }

    @PrePersist
    private void prePersist(){
        setDate(new Date());
        setLook(false);
    }
}
