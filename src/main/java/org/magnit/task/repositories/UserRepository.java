package org.magnit.task.repositories;

import org.magnit.task.entities.Roles;
import org.magnit.task.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findTop3ByOrderByIdeaCountDesc();
    List<User> findAllByRole(Roles role);

    User findByUsername(String username);

    @Override
    long count();
}
