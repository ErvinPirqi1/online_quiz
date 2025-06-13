package dev.ervin.online_quiz.dtos;

import lombok.Getter;
import lombok.Setter;

// AnswerDto.java
@Getter
@Setter
public class AnswerDto {
    private Long id;
    private String text;
    private Boolean isCorrect;

    public AnswerDto() {}

    public AnswerDto(Long id, String text, Boolean isCorrect) {
        this.id = id;
        this.text = text;
        this.isCorrect = isCorrect;
    }
}
