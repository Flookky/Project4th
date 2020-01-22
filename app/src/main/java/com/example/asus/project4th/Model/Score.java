package com.example.asus.project4th.Model;

public class Score {
    private String assignname;
    private String subjectID;
    private String Username;
    private int score;

    public Score(){
    }

    public Score(String assignname, String subjectID, String Username, int score) {
        this.assignname = assignname;
        this.subjectID = subjectID;
        this.Username = Username;
        this.score = score;
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

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
