package com.example.asus.project4th.Model;

public class Post {

    private String numberPost;
    private String postTitle;
    private String postContent;
    private String time;
    //private String totalPost;

    public Post() {
    }

    public Post(String postTitle, String postContent, String time) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.time = time;
        //this.totalPost = "0";
    }

    public String getNumberPost(){
        return numberPost;
    }
    public void setNumberPost(String numberPost) {
        this.numberPost = numberPost;
    }

    public String getPostTitle(){
        return postTitle;
    }
    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent(){
        return postContent;
    }
    public void setPostContent(String postContent){
        this.postContent = postContent;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }


}
