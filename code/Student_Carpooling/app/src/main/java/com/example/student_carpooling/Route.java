package com.example.student_carpooling;


import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsLeg;

class Route {
    //this consists of the route and its information
    //the route drawn is know as a polyline, and the directions from the origin to that
    //dest is referred to as directionsLeg

    private Polyline polyline;
    private DirectionsLeg directionsLeg;


    Route(Polyline polyline, DirectionsLeg directionsLeg){
        this.directionsLeg = directionsLeg;
        this.polyline = polyline;
    }

    //add setters and getters
    DirectionsLeg getDirectionsLeg() {
        return directionsLeg;
    }

    Polyline getPolyline() {
        return polyline;
    }
    //to string method for the marker window?
}
