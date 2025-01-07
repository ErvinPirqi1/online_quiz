package dev.ervin.online_quiz.dtos;


import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {

    private Long id;

    @NotNull(message = "Quiz must be provided.")
    private Quiz quiz;

    @NotNull(message = "User must be provided.")
    private User user;

    @NotBlank(message = "Question text cannot be blank.")
    @Size(max = 500, message = "Question text must not exceed 500 characters.")
    private String question;

    @NotNull(message = "Question type must be provided.")
    @Min(value = 0, message = "Invalid question type. Minimum value is 0.")
    @Max(value = 2, message = "Invalid question type. Maximum value is 2.") // Adjust based on your range of question types
    private Short questionType;

    @PastOrPresent(message = "Creation date cannot be in the future.")
    private LocalDateTime createdAt;

    @PastOrPresent(message = "Modification date cannot be in the future.")
    private LocalDateTime modifiedAt;

    @NotNull(message = "CreatedBy must be provided.")
    private User createdBy;

    private User modifiedBy; // Nullable, so no validation required

    @NotNull(message = "IsDeleted flag must be provided.")
    private Boolean isDeleted;

}
