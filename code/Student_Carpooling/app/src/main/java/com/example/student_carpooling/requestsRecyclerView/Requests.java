package com.example.student_carpooling.requestsRecyclerView;

public class Requests {


    private String UserID;
    private String ProfilePicUrl;
    private float Longitude;
    private float Latitude;
    private String Username;
    private String Fullname;


    public Requests(String UserID,String ProfilePicUrl,float Longitude, float Latitude, String Username,String Fullname){
        this.UserID = UserID;
        this.ProfilePicUrl = ProfilePicUrl;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Username = Username;
        this.Fullname = Fullname;
    }

    public Requests(){}

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public String getProfilePicUrl() {
        return ProfilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        ProfilePicUrl = profilePicUrl;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUsername() {
        return Username;
    }

    public float getLatitude() {
        return Latitude;
    }

    public float getLongitude() {
        return Longitude;
    }

    public String getUserID() {
        return UserID;
    }

    public void setLatitude(float latitude) {
        Latitude = latitude;
    }

    public void setLongitude(float longitude) {
        Longitude = longitude;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
