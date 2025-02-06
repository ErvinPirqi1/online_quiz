package dev.ervin.online_quiz.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionDto {
    private Long id;
    private Long quizId;
    private String questionText;
    private Short questionType;
    private List<AnswerDto> answers;


    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", questionType=" + questionType +
                ", answers=" + answers +
                '}';
    }
}