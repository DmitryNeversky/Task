package org.magnit.task.repositories;

import org.magnit.task.entities.Notification;
import org.magnit.task.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByLookAndUser(boolean look, User user);
    Notification findById(long id);
}
