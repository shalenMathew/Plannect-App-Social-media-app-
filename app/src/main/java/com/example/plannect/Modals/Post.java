package com.example.plannect.Modals;

public class Post {

    String id;
    String post;
    String description;
    String profileImg;
    String username;

    String postId;

    public Post(String id, String post, String description, String profileImg, String username, String postId) {
        this.id = id;
        this.post = post;
        this.description = description;
        this.profileImg = profileImg;
        this.username = username;
        this.postId = postId;
    }

    public Post() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
