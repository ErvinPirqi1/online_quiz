package dev.ervin.online_quiz.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionDto {
    private long id;
    private String questionText;
    private Short questionType; // 0 = Multiple Choice, 1 = True/False
    private List<OptionDto> options;
    private Integer correctAnswer; // Index of the correct answer
}
