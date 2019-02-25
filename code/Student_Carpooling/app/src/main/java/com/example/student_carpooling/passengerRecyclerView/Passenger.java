package com.example.student_carpooling.passengerRecyclerView;

public class Passenger {


    private String ID;
    private String profilePicUrl;
    private String UserName;


    public Passenger(String ID, String profilePicUrl, String UserName){
        this.profilePicUrl = profilePicUrl;
        this.UserName = UserName;
        this.ID = ID;

    }

    public Passenger(){
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
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
