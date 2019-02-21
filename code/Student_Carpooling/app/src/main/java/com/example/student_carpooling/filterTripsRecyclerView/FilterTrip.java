package com.example.student_carpooling.filterTripsRecyclerView;

public class FilterTrip {

    private String profilePicUrl;
    private String Date;
    private String Time;
    private String UserName;
    private String Seats;
    private String LuggageCheck;
    private String Note;
    private String Name;
    private String Starting;
    private String Destination;
    private String ID;



    public FilterTrip(String ID, String Note, String LuggageCheck, String Name,String profilePicUrl, String Date,String Time, String Seats, String UserName, String Starting, String Destination ){
        this.Date = Date;
        this.Time = Time;
        this.UserName = UserName;
        this.Seats = Seats;
        this.profilePicUrl = profilePicUrl;
        this.Starting = Starting;
        this.Destination = Destination;
        this.Note = Note;
        this.LuggageCheck = LuggageCheck;
        this.Name = Name;
        this.ID = ID;

    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public String getLuggageCheck() {
        return LuggageCheck;
    }

    public void setLuggageCheck(String luggageCheck) {
        LuggageCheck = luggageCheck;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getNote() {
        return Note;
    }


    //may not need the set functions

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserName(){
        return UserName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getSeats() {
        return Seats;
    }

    public void setSeats(String seats) {
        this.Seats = seats;
    }

    //public void setLuggageCheck(String luggageCheck) {
      //  this.LuggageCheck = luggageCheck;
    //}

    public void setDestination(String destination) {
        this.Destination = destination;
    }

    public void setStarting(String starting) {
        this.Starting = starting;
    }

    public String getStarting(){
        return Starting;
    }

    //public void setUserName(String userName) {
    //UserName = userName;
    //}

    public String getDestination() {
        return Destination;
    }

    public String getTime(){
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    //public String getUserName(){
    //return UserName;
    //}

   // public String getLuggageCheck() {
   //     return LuggageCheck;
    //}
}
