package com.example.student_carpooling.filterTripsRecyclerView;

public class FilterTrip {

    private String profilePicUrl;
    private String Date;
    private String Time;
    private String UserName;
<<<<<<< HEAD



    public FilterTrip(String UserName){
        this.UserName = UserName;

=======
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
>>>>>>> a55d60a77aa0c4f8f9f61a406944db63df7d98b2

    }

    //may not need the set functions

    public String getUserName(){
        return UserName;
    }

}

