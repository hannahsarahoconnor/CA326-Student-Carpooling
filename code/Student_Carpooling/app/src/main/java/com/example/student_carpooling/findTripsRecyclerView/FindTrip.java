package com.example.student_carpooling.findTripsRecyclerView;

public class FindTrip {


    private String Fullname;
    private String username;
    private String profilePicUrl;
    private String time;
    private String date;
    private String starting;
    private String destination;
    private String seats;
    private String Luggage;
    private String Note;
    private String ID;
    private String TripID;
    private String CurrentID;

    public FindTrip(String CurrentID, String TripID, String Fullname, String username,String profilePicUrl,String time,String date,String starting,String destination, String seats, String Luggage, String Note,String ID) {
        this.Fullname = Fullname;
        this.username = username;
        this.profilePicUrl = profilePicUrl;
        this.time = time;
        this.date = date;
        this.destination = destination;
        this.starting = starting;
        this.seats = seats;
        this.Luggage = Luggage;
        this.Note = Note;
        this.ID = ID;
        this.TripID = TripID;
        this.CurrentID = CurrentID;
    }

    public String getCurrentID() {
        return CurrentID;
    }

    public void setCurrentID(String currentID) {
        CurrentID = currentID;
    }

    public String getTripID() {
        return TripID;
    }

    public void setTripID(String tripID) {
        TripID = tripID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getLuggage() {
        return Luggage;
    }

    public void setLuggage(String luggage) {
        Luggage = luggage;
    }


    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public String getFullname() {
        return Fullname;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public void setStarting(String starting) {
        this.starting = starting;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDestination() {
        return destination;
    }

    public String getSeats() {
        return seats;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStarting() {
        return starting;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
