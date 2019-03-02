package com.example.student_carpooling;

import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsLeg;

public class Route {
    //this consists of the route and its information
    //the route drawn is know as a polyline, and the directions from the origin to that
    //dest is referred to as directionsLeg

    private Polyline polyline;
    private DirectionsLeg directionsLeg;


    public Route(Polyline polyline, DirectionsLeg directionsLeg){
        this.directionsLeg = directionsLeg;
        this.polyline = polyline;
    }

    //add setters and getters


    public DirectionsLeg getDirectionsLeg() {
        return directionsLeg;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setDirectionsLeg(DirectionsLeg directionsLeg) {
        this.directionsLeg = directionsLeg;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    //to string method for the marker window?

    @Override
    public String toString() {
        return "polyline"+ polyline + " leg" + directionsLeg;
    }
}
