package com.example.asus.project4th.Model;

public class Choice2 {

    private String numberQuestion;
    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private String type;
    private String answer;
    private String answeris;
    private String status;
    private String questionID;

    public Choice2() {
    }

    public Choice2(String numberQuestion, String question, String choiceA, String choiceB, String choiceC, String choiceD, String type, String answer, String answeris, String status, String questionID) {
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.type = type;
        this.answer = answer;
        this.answeris = answeris;
        this.status = status;
        this.questionID = questionID;
    }

    public Choice2(String numberQuestion, String question, String choiceA, String choiceB, String choiceC, String choiceD, String type, String answer, String answeris, String status) {
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.type = type;
        this.answer = answer;
        this.answeris = answeris;
        this.status = status;
    }

    public Choice2(String numberQuestion, String question, String choiceA, String choiceB, String choiceC, String choiceD, String type, String answer, String answeris) {
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.type = type;
        this.answer = answer;
        this.answeris = answeris;
    }

    public Choice2(String numberQuestion, String question, String choiceA, String choiceB, String type, String answer, String answeris, String status){
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.type = type;
        this.answer = answer;
        this.answeris = answeris;
        this.status = status;
    }

    public Choice2(String numberQuestion, String question, String choiceA, String choiceB, String type, String answer, String answeris){
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.type = type;
        this.answer = answer;
        this.answeris = answeris;
    }

    public Choice2(String numberQuestion, String question, String answer, String answeris) {
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.answer = answer;
        this.answeris = answeris;
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

    public String getChoiceA() {
        return choiceA;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
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

    public String getAnsweris() { return answeris; }

    public void setAnsweris(String answeris) { this.answeris = answeris; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getQuestionID() { return questionID; }

    public void setQuestionID(String questionID) { this.questionID = questionID; }

}

