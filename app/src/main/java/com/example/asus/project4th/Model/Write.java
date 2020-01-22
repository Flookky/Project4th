package com.example.asus.project4th.Model;

public class Write {
    private String numberQuestion;
    private String question;
    private String type;
    private String answer;
    private String status;

    public Write() {
    }

    public Write(String numberQuestion, String question, String type, String answer) {
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.type = type;
        this.answer = answer;
    }

    public Write(String numberQuestion, String question, String type, String answer, String status) {
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.type = type;
        this.answer = answer;
        this.status = status;
    }

    public String getNumberQuestion() {
        return numberQuestion;
    }

    public void setNumberQuestion(String numberQuestion) {
        this.numberQuestion = numberQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}


