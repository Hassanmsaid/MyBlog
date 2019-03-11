package com.example.blog.Model;

public class Blog {
    private String userID, blogImage, blogTitle, blogText, timeStamp;

    public Blog() {
    }

    public Blog(String userID, String blogImage, String blogTitle, String blogText, String timeStamp) {
        this.userID = userID;
        this.blogImage = blogImage;
        this.blogTitle = blogTitle;
        this.blogText = blogText;
        this.timeStamp = timeStamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBlogImage() {
        return blogImage;
    }

    public void setBlogImage(String blogImage) {
        this.blogImage = blogImage;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getBlogText() {
        return blogText;
    }

    public void setBlogText(String blogText) {
        this.blogText = blogText;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
