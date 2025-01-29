package dev.ervin.online_quiz.dtos;

import dev.ervin.online_quiz.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String username;
    private String name;
    private String surname;
    private String email;
    private Role role;
}
