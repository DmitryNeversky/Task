package org.magnit.task.services;

import org.magnit.task.entities.Idea;
import org.magnit.task.entities.IdeaStatus;
import org.magnit.task.entities.User;
import org.magnit.task.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService{

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int ideasCount(User user){

        return user.getIdeas().size();
    }

    public int ideaStatusCount(User user, IdeaStatus status){

        int count = 0;

        for(Idea idea : user.getIdeas())
            if(idea.getStatus() == status)
                count++;

        return count;
    }
}
