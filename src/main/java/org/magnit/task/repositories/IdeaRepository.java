package org.magnit.task.repositories;

import org.magnit.task.entities.Idea;
import org.magnit.task.entities.IdeaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Integer> {
    Idea findById(int id);
    Page<Idea> findAll(Pageable pageable);
    Idea findByStatus(IdeaStatus status);
}
