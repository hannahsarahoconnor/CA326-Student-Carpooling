package com.example.student_carpooling.usersRecyclerView;

public class User {

    private String ID;
    private String profilePicUrl;
    private String UserName;


    public User(String ID, String profilePicUrl, String UserName){
        this.profilePicUrl = profilePicUrl;
        this.UserName = UserName;
        this.ID = ID;

    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserName() {
        return UserName;
    }
}
