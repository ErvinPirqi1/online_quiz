package dev.ervin.online_quiz.helpers; // Choose a suitable package

import dev.ervin.online_quiz.dtos.QuestionDto;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class QuestionForm {
    private List<QuestionDto> questions;

    @Override
    public String toString() {  // Important for debugging!
        return "QuestionForm{" +
                "questions=" + questions +
                '}';
    }
}