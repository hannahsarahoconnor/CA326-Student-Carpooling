package com.example.student_carpooling.filterTripsRecyclerView;

public class FilterTrip {

    private String profilePicUrl;
    private String Date;
    private String Time;
    private String UserName;



    public FilterTrip(String UserName){
        this.UserName = UserName;


    }

    //may not need the set functions

    public String getUserName(){
        return UserName;
    }

}

