package dev.ervin.online_quiz.dtos;

import dev.ervin.online_quiz.models.User;
import jakarta.validation.constraints.Size;


import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDto {

    private Long id;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Title cannot be null")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotNull(message = "Description cannot be null")
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    private String description;

    @NotNull(message = "Image cannot be null")
    private String img;

    @NotNull(message = "Category cannot be null")
    @Size(min = 1, max = 50, message = "Category must be between 1 and 50 characters")
    private String category;

    @NotNull(message = "Visibility cannot be null")
    @Min(value = 0, message = "Visibility must be 0 (private) or 1 (public)")
    @Max(value = 1, message = "Visibility must be 0 (private) or 1 (public)")
    private Short visibility; // 0=private, 1=public

    @NotNull(message = "Created At cannot be null")
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @NotNull(message = "Created By ID cannot be null")
    private String createdByUsername;

    private String modifiedByUsername;

    @NotNull(message = "Deleted status cannot be null")
    private Boolean isDeleted;
}
