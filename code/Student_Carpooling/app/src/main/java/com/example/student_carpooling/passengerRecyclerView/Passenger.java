package com.example.student_carpooling.passengerRecyclerView;

public class Passenger {


    private String ID;
    private String profilePicUrl;
    private String UserName;
    private String NotificationKey;
    private float lat;
    private float lon;
    private String Fullname;
    private float DLat;
    private float DLon;
    private String TripID;
    private String DriverUN;
    private String Type;


    public Passenger(String Type, String DriverUN,String TripID, float DLat, float DLon, String Fullname,String ID, String profilePicUrl, String UserName, float lat, float lon, String NotificationKey){
        this.profilePicUrl = profilePicUrl;
        this.TripID = TripID;
        this.Fullname = Fullname;
        this.UserName = UserName;
        this.ID = ID;
        this.DriverUN = DriverUN;
        this.NotificationKey = NotificationKey;
        this.lat = lat;
        this.lon = lon;
        this.DLat = DLat;
        this.DLon = DLon;
        this.Type = Type;

    }
    public Passenger(){
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    String getDriverUN() {
        return DriverUN;
    }

    String getTripID() {
        return TripID;
    }


    float getDLat() {
        return DLat;
    }

    float getDLon() {
        return DLon;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public String getFullname() {
        return Fullname;
    }

    String getNotificationKey() {
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


    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    String getUserName() {
        return UserName;
    }
}
