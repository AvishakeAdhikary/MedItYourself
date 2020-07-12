package com.example.med_it_yourself.HistoryRecyclerView;

public class HistoryObject {
    private String rideId;

    public HistoryObject(String rideId){
        this.rideId = rideId;
    }

    public String getRideId(){return rideId;}
    public void setRideId(String rideId) {
        this.rideId = rideId;
    }
}
