package org.magnit.task.repositories;

import org.magnit.task.entities.Idea;
import org.magnit.task.entities.IdeaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Integer>, JpaSpecificationExecutor<Idea> {
    Idea findById(int id);

    @Query("SELECT i FROM Idea i WHERE i.title LIKE %?1%")
    List<Idea> findAllByKeyWord(String keyword);
}
