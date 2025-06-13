package dev.ervin.online_quiz.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// QuestionDto.java
@Getter
@Setter
public class QuestionDto {
    private Long id;
    private Long quizId;
    private String questionText;
    private Short questionType;
    private List<AnswerDto> answers;

    public QuestionDto() {}

    public QuestionDto(Long id, Long quizId, String questionText, Short questionType, List<AnswerDto> answers) {
        this.id = id;
        this.quizId = quizId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.answers = answers;
    }
}
