package com.example.student_carpooling.passengerTripsRecyclerView;


public class PassengerTrip {

    private String DriverPicUrl;
    private String TripId;
    private String DriverID;
    private float lat;
    private float lon;
    private float dstlat;
    private float dstlon;
    private String Starting;
    private String Destination;
    private String Time;
    private String Date;
    private String DriverUsername;
    private String DriverName;
    private String NotificationKey;


    public PassengerTrip(String NotificationKey,String DriverName, String DriverPicUrl,String DriverUsername, String TripId, String DriverID, float lat, float lon, float dstlat, float dstlon, String Starting,String Destination, String Time, String Date){
        this.DriverName = DriverName;
        this.DriverUsername = DriverUsername;
        this.TripId = TripId;
        this.Date = Date;
        this.Destination = Destination;
        this.dstlat = dstlat;
        this.dstlon = dstlon;
        this.lat = lat;
        this.lon = lon;
        this.Starting = Starting;
        this.DriverID = DriverID;
        this.Time = Time;
        this.DriverPicUrl= DriverPicUrl;
        this.NotificationKey = NotificationKey;

    }

    public PassengerTrip(){}

    public String getNotificationKey() {
        return NotificationKey;
    }

    public void setNotificationKey(String notificationKey) {
        NotificationKey = notificationKey;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getDriverName() {
        return DriverName;
    }

    public String getDriverPicUrl() {
        return DriverPicUrl;
    }

    public void setDriverPicUrl(String driverPicUrl) {
        DriverPicUrl = driverPicUrl;
    }

    public void setDriverUsername(String driverUsername) {
        DriverUsername = driverUsername;
    }

    public String getDriverUsername() {
        return DriverUsername;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setDriverID(String driverID) {
        DriverID = driverID;
    }

    public String getDriverID() {
        return DriverID;
    }

    public String getStarting() {
        return Starting;
    }

    public String getDate() {
        return Date;
    }

    public String getDestination() {
        return Destination;
    }

    public void setStarting(String starting) {
        Starting = starting;
    }

    public void setDate(String date) {
        Date = date;
    }

    public float getDstlat() {
        return dstlat;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public void setTime(String time) {
        Time = time;
    }

    public float getDstlon() {
        return dstlon;
    }

    public float getLat() {
        return lat;
    }

    public String getTripId() {
        return TripId;
    }

    public float getLon() {
        return lon;
    }

    public String getTime() {
        return Time;
    }

    public void setDstlat(float dstlat) {
        this.dstlat = dstlat;
    }

    public void setDstlon(float dstlon) {
        this.dstlon = dstlon;
    }

    public void setTripId(String tripId) {
        TripId = tripId;
    }
}
