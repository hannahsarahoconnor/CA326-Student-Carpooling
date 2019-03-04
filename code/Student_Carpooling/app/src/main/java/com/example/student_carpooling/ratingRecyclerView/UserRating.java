package com.example.student_carpooling.ratingRecyclerView;

public class UserRating {
    private String ID;
    private String Username;
    private String PicUrl;


    public UserRating(String ID, String PicUrl, String Username){
        this.PicUrl = PicUrl;
        this. Username =  Username;
        this.ID = ID;

    }

    public UserRating(){

    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getPicUrl() {
        return PicUrl;
    }
}
