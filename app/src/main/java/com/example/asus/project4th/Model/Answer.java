package com.example.asus.project4th.Model;

public class Answer {
    private String username;
    private String assignname;
    private String subjectID;
    private int score;
    private String name;

    public Answer() {
    }

    public Answer(String username, String assignname, String subjectID, int score, String name) {
        this.username = username;
        this.assignname = assignname;
        this.subjectID = subjectID;
        this.score = score;
        this.name = name;
    }

    public Answer(String username, String assignname, String subjectID, int score) {
        this.username = username;
        this.assignname = assignname;
        this.subjectID = subjectID;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getAssignname() {
        return assignname;
    }

    public void setAssignname(String assignname) {
        this.assignname = assignname;
    }


    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
