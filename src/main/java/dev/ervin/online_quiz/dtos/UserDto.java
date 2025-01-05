package dev.ervin.online_quiz.dtos;

import dev.ervin.online_quiz.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private Short role; // 0=admin, 1=teacher, 2=student

}
