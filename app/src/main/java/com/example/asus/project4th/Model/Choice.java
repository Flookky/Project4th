package com.example.asus.project4th.Model;

public class Choice {

    private String numberQuestion;
    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private String type;
    private String answer;
    private String status;
    private boolean checked;

    public Choice() {
    }


    public Choice(String numberQuestion, String question, String choiceA, String choiceB, String choiceC, String choiceD, String type, String answer, String status, boolean checked) {
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.type = type;
        this.answer = answer;
        this.status = status;
        this.checked = checked;
    }

    public Choice(String numberQuestion, String question, String choiceA, String choiceB, String choiceC, String choiceD, String type, String answer, String status) {
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.type = type;
        this.answer = answer;
        this.status = status;
    }

    public Choice(String numberQuestion, String question, String choiceA, String choiceB, String choiceC, String choiceD, String type, String answer) {
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.type = type;
        this.answer = answer;
    }

    public Choice(String numberQuestion, String question, String answer) {
        this.numberQuestion = numberQuestion;
        this.question = question;
        this.answer = answer;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getChecked(){
        return checked;
    }

    public void setChecked(boolean checked){
        this.checked = checked;
    }

}
