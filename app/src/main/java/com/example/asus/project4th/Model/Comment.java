package com.example.asus.project4th.Model;

public class Comment {

    private String numberPost;
    private String commenttxt;
    private String image;
    private String file;
    private String fileUrl;
    private String Username;
    private String time;

    public Comment(){

    }

    public Comment(String commenttxt, String image, String file, String fileUrl, String time, String Username) {
        this.commenttxt = commenttxt;
        this.image = image;
        this.file = file;
        this.fileUrl = fileUrl;
        this.time = time;
        this.Username = Username;
    }

    public Comment(String commenttxt, String image, String file, String time, String Username) {
        this.commenttxt = commenttxt;
        this.image = image;
        this.file = file;
        this.time = time;
        this.Username = Username;
    }

    public Comment(String commenttxt, String image, String file, String time) {
        this.commenttxt = commenttxt;
        this.image = image;
        this.file = file;
        this.time = time;
    }

    public Comment(String commenttxt, String Username, String time) {
        this.commenttxt = commenttxt;
        this.Username = Username;
        this.time = time;
    }

    public String getNumberPost(){
        return numberPost;
    }
    public void setNumberPost(String numberPost) {
        this.numberPost = numberPost;
    }

    public String getCommenttxt(){
        return commenttxt;
    }
    public void setCommenttxt(String commenttxt) {
        this.commenttxt = commenttxt;
    }

    public String getImage(){
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getFile(){
        return file;
    }
    public void setFile(String file){
        this.file = file;
    }

    public String getFileUrl(){
        return fileUrl;
    }
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUsername(){
        return Username;
    }
    public void setUsername(String Username){
        this.Username = Username;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }


}
