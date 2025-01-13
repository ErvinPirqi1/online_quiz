package dev.ervin.online_quiz.services;

import dev.ervin.online_quiz.models.User;

public interface UserService extends BaseService<User, Long> {
    
    User findByUsername(String username);

}
