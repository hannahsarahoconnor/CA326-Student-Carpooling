package com.example.student_carpooling.tripRequestsRecyclerView;

public class RequestTrip {

    private String ID;
    private String Date;
    private String Time;
    private String Username;
    private String LuggageCheck;
    private String Starting;
    private String Destination;
    private String Note;
    private String profilePicUrl;
    private String Fullname;
    private String Type;
    private String RequestID;


    public RequestTrip(String RequestID, String Type,String Note, String Username, String ID,String profilePicUrl,String Date,String Time,String Fullname,String LuggageCheck, String Starting, String Destination ){
        this.Type = Type;
        this.RequestID = RequestID;
        this.Date = Date;
        this.Time = Time;
        this.Username = Username;
        this.LuggageCheck = LuggageCheck;
        this.Starting = Starting;
        this.Destination = Destination;
        this.ID = ID;
        this.Note = Note;
        this.Fullname = Fullname;
        this.profilePicUrl = profilePicUrl;

    }

    String getRequestID() {
        return RequestID;
    }

    public String getType() {
        return Type;
    }

    public String getTime() {
        return Time;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUsername() {
        return Username;
    }

    public String getID() {
        return ID;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setStarting(String starting) {
        Starting = starting;
    }

    public String getDestination() {
        return Destination;
    }

    public String getDate() {
        return Date;
    }

    public String getStarting() {
        return Starting;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }


    public void setNote(String note) {
        Note = note;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getNote() {
        return Note;
    }


    String getLuggageCheck() {
        return LuggageCheck;
    }
}
