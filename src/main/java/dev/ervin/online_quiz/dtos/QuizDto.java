package dev.ervin.online_quiz.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDto {

    private Long id;
    private Long userId;
    private Long createdBy;
    private String title;
    private String description;
    private String category;
    private Short visibility;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String createdByUsername;
    private String modifiedByUsername;
    private Boolean isDeleted = false;
    private Short status = 1;
}
