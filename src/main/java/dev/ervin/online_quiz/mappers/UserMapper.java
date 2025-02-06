package dev.ervin.online_quiz.mappers;

import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.infrastructure.mapping.SimpleMapper;
import dev.ervin.online_quiz.models.User;
import org.mapstruct.Mapper;

//@Mapper
public interface UserMapper extends SimpleMapper<User, UserDto> {
    User fromUserRegistrationDto(UserRegistrationRequestDto userRegDto);
}
