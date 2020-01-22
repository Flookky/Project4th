package com.example.asus.project4th.Model;

public class Student {
    private String name;
    private String username;

    public Student() {
    }

    public Student(String username,String name) {
        this.username = username;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
