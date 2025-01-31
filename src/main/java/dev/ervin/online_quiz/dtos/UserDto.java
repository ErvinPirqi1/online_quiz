package dev.ervin.online_quiz.dtos;

import dev.ervin.online_quiz.models.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    @NotNull(message = "User ID cannot be null")
    private Long id;

    @NotNull(message = "Username cannot be null")
    private String username;

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Surname cannot be null")
    private String surname;

    @NotNull(message = "Email cannot be null")
    private String email;

    @NotNull(message = "User Role cannot be null")
    private Role role;

}
