package dev.ervin.online_quiz.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDto {
    private Long id;
    private String text;
    private Boolean isCorrect; // Now a Boolean
}