package com.example.student_carpooling.passengerRecyclerView;

public class Passenger {


    private String ID;
    private String profilePicUrl;
    private String UserName;
    private String NotificationKey;
    private float lat;
    private float lon;
    private String Fullname;


    public Passenger(String Fullname,String ID, String profilePicUrl, String UserName, float lat, float lon, String NotificationKey){
        this.profilePicUrl = profilePicUrl;
        this.Fullname = Fullname;
        this.UserName = UserName;
        this.ID = ID;
        this.NotificationKey = NotificationKey;
        this.lat = lat;
        this.lon = lon;

    }
    public Passenger(){
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setNotificationKey(String notificationKey) {
        NotificationKey = notificationKey;
    }

    public String getNotificationKey() {
        return NotificationKey;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
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
