package dev.ervin.online_quiz.mappers.impls;

import dev.ervin.online_quiz.dtos.UserDto;
import dev.ervin.online_quiz.dtos.UserRegistrationRequestDto;
import dev.ervin.online_quiz.mappers.UserMapper;
import dev.ervin.online_quiz.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
    private final PasswordEncoder passwordEncoder;

    public UserMapperImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User fromUserRegistrationDto(UserRegistrationRequestDto userRegDto) {
        User user = new User();
        user.setUsername(userRegDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRegDto.getPassword()));  // Encrypt the password here
        user.setEmail(userRegDto.getEmail());
        user.setName(userRegDto.getName());
        user.setSurname(userRegDto.getSurname());
        user.setRole(userRegDto.getRole());
        return user;
    }

    @Override
    public User toEntity(UserDto userDto) {
        User user =  new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setRole(userDto.getRole());
        user.setEmail(userDto.getEmail());
        return user;
    }

    @Override
    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setName(user.getName());
        userDto.setSurname(user.getSurname());
        userDto.setRole(user.getRole());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

}
