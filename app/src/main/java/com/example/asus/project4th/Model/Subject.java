package com.example.asus.project4th.Model;

public class Subject {

    private String subjectID;
    private String subjectName;
    private String username;
    private String name;
    private String time;
    private String teacherusername;

    public Subject() {
    }

    public Subject(String subjectID, String subjectname, String username, String name, String time, String teacherusername) {
        this.subjectID = subjectID;
        this.subjectName = subjectname;
        this.username = username;
        this.name = name;
        this.time = time;
        this.teacherusername = teacherusername;
    }

    public Subject(String subjectID, String subjectname, String username, String name, String time) {
        this.subjectID = subjectID;
        this.subjectName = subjectname;
        this.username = username;
        this.name = name;
        this.time = time;
    }

    public Subject(String subjectID, String subjectname, String username, String time) {
        this.subjectID = subjectID;
        this.subjectName = subjectname;
        this.username = username;
        this.time = time;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getSubjectname() {
        return subjectName;
    }

    public void setSubjectname(String subjectname) {
        this.subjectName = subjectname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTeacherusername() {
        return teacherusername;
    }

    public void setTeacherusername(String teacherusername) {
        this.teacherusername = teacherusername;
    }
}