package dev.ervin.online_quiz.services;


import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.models.User;

public interface UserService extends BaseService<User, Long> {

    void registerUser(UserRegistrationRequestDto userRegisterDto);

    UserDto getUserDetails(String username);

    User getUserByUsername(String username);
}
