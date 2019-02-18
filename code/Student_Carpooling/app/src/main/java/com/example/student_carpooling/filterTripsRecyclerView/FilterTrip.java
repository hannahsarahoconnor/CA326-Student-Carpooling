package com.example.student_carpooling.filterTripsRecyclerView;

public class FilterTrip {

    private String TripID;
    private String Date;
    private String Time;
    private String UserName;
    private String Seats;
    //private String LuggageCheck;
    private String Starting;
    private String Destination;



    public FilterTrip(String Date,String Time, String Seats, String UserName, String Starting, String Destination ){
        this.Date = Date;
        this.Time = Time;
        this.UserName = UserName;
        this.Seats = Seats;
        //this.LuggageCheck = LuggageCheck;
        this.Starting = Starting;
        this.Destination = Destination;

    }

    //may not need the set functions


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
