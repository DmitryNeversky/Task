package org.magnit.task.repositories;

import org.magnit.task.entities.Idea;
import org.magnit.task.entities.IdeaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Integer> {
    Idea findById(int id);
    Page<Idea> findAll(Pageable pageable);
    Page<Idea> findAllByStatus(Pageable pageable, IdeaStatus status);
    List<Idea> findByStatus(IdeaStatus status);

    @Query("SELECT i FROM Idea i WHERE i.title LIKE %?1%")
    List<Idea> findAllByKeyWord(String keyword);
}
