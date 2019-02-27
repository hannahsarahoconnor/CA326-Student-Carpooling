package com.example.student_carpooling.requestsRecyclerView;

import com.example.student_carpooling.tripRecyclerView.Trip;

public class Requests {

    private String DriverUsername;
    private String TripID;
    private String UserID;
    private String ProfilePicUrl;
    private float Longitude;
    private float Latitude;
    private String Username;
    private String Fullname;
    private String NotificationKey;


    public Requests(String DriverUsername,String NotificationKey,String TripID,String UserID,String ProfilePicUrl,float Longitude, float Latitude, String Username,String Fullname){
        this.UserID = UserID;
        this.DriverUsername = DriverUsername;
        this.TripID = TripID;
        this.ProfilePicUrl = ProfilePicUrl;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Username = Username;
        this.Fullname = Fullname;
        this.NotificationKey = NotificationKey;
    }

    public Requests(){}

    public String getDriverUsername() {
        return DriverUsername;
    }

    public void setDriverUsername(String driverUsername) {
        DriverUsername = driverUsername;
    }

    public String getNotificationKey() {
        return NotificationKey;
    }

    public void setNotificationKey(String notificationKey) {
        NotificationKey = notificationKey;
    }

    public void setTripID(String tripID) {
        TripID = tripID;
    }

    public String getTripID() {
        return TripID;
    }

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
