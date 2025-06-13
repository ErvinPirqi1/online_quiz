package dev.ervin.online_quiz.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuizDto {
    private String title;
    private String description;
    private String category;
    private Short visibility;
}

