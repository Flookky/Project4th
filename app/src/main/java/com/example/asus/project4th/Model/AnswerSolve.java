package com.example.asus.project4th.Model;

import java.util.Queue;

public class AnswerSolve {
    private String answer;
    private String answertext;
    private String check;
    private String question;
    private String numberQuestion;

    public AnswerSolve() {
    }

    public AnswerSolve(String answer, String answertext, String check, String question, String numberQuestion) {
        this.answer = answer;
        this.answertext = answertext;
        this.check = check;
        this.question = question;
        this.numberQuestion = numberQuestion;
    }

    public AnswerSolve(String answer, String answertext, String check, String question) {
        this.answer = answer;
        this.answertext = answertext;
        this.check = check;
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswertext() {
        return answertext;
    }

    public void setAnswertext(String answertext) {
        this.answertext = answertext;
    }

    public String getNumberQuestion() {
        return numberQuestion;
    }

    public void setNumberQuestion(String numberQuestion) {
        this.numberQuestion = numberQuestion;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

}
