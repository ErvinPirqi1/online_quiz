package dev.ervin.online_quiz.mappers.impls;

import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.mappers.UserMapper;
import dev.ervin.online_quiz.models.User;

public class UserMapperImpl implements UserMapper {
    @Override
    public User fromUserRegistrationDto(UserRegistrationRequestDto userRegistrationRequestDto) {
        User user = new User();
        user.setUsername(userRegistrationRequestDto.getUsername());
        user.setName(userRegistrationRequestDto.getName());
        user.setSurname(userRegistrationRequestDto.getSurname());
        user.setEmail(userRegistrationRequestDto.getEmail());
        user.setPassword(userRegistrationRequestDto.getPassword());
        user.setRole(userRegistrationRequestDto.getRole());
        return user;
    }

    @Override
    public User toEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        return user;
    }

    @Override
    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setName(user.getName());
        userDto.setSurname(user.getSurname());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        return userDto;
    }
}
