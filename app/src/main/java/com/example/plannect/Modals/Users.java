package com.example.plannect.Modals;

public class Users {

    String username;
    String description;
    String id;
    String profileImg;
    String status;

    public Users(String username, String description, String id, String profileImg, String status) {
        this.username = username;
        this.description = description;
        this.id = id;
        this.profileImg = profileImg;
        this.status = status;
    }

    public Users() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
