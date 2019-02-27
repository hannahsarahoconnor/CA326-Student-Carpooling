package com.example.student_carpooling.seatRecyclerView;

public class Seat {
    private String Number;


    public Seat(String Number){
        this.Number = Number;
    }

    public Seat(){}


    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }
}
