package com.example.student_carpooling.usersRecyclerView;

public class User {

    private String profilePicUrl;
    private String UserName;


    public User(String profilePicUrl, String UserName){
        this.profilePicUrl = profilePicUrl;
        this.UserName = UserName;

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
