package org.magnit.task.repositories;

import org.magnit.task.entities.Idea;
import org.magnit.task.entities.IdeaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Integer>, JpaSpecificationExecutor<Idea> {
    Page<Idea> findById(Pageable pageable, int id);
    Idea findById(int id);

    List<Idea> findTop3ByOrderByLikeCountDesc();
    List<Idea> findTop3ByOrderByIdDesc();

    Page<Idea> findAll(Pageable pageable);
    Page<Idea> findAllByStatus(Pageable pageable, IdeaStatus status);
    Page<Idea> findAllByTitleContaining(Pageable pageable, String title);
    Page<Idea> findByTitleContainingAndStatus(Pageable pageable, String title, IdeaStatus status);
}
