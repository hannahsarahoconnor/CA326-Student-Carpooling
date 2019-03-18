package com.example.student_carpooling.usersRecyclerView;

public class User {

    private String ID;
    private String profilePicUrl;
    private String UserName;
    private String Fullname;


    public User(String ID, String profilePicUrl, String UserName, String Fullname){
        this.profilePicUrl = profilePicUrl;
        this.UserName = UserName;
        this.ID = ID;
        this.Fullname = Fullname;

    }

    public User(){

    }

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    String getUserName() {
        return UserName;
    }
}
