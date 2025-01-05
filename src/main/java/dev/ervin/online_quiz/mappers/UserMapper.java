package dev.ervin.online_quiz.mappers;

import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.infrastructure.mapping.SimpleMapper;
import dev.ervin.online_quiz.models.User;

public interface UserMapper extends SimpleMapper<User, UserDto> {
    User fromUserRegistrationDto(UserRegistrationRequestDto userRegistrationRequestDto);
}
