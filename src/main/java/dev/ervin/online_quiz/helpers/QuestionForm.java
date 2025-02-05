package dev.ervin.online_quiz.helpers;

import dev.ervin.online_quiz.dtos.QuestionDto;

import java.util.List;

public class QuestionForm {
    private List<QuestionDto> questions;

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }
}
