package com.api.optiplein.services.optipleinServices;

import com.api.optiplein.Models.Coordinates;
import com.api.optiplein.Models.preparation.PDV;
import com.api.optiplein.maths.MathGlobeCalculation;

public class DeltaCalculation {

    private Coordinates middle;
    private Coordinates departure;
    private Coordinates arrival;
    private PDV pdv;

    DeltaCalculation(Coordinates middle, Coordinates departure, Coordinates arrival, PDV pdv){
        this.middle = middle;
        this.departure = departure;
        this.arrival = arrival;
        this.pdv = pdv;
    }

    private MathGlobeCalculation mathGlobeCalculation;
    private MathGlobeCalculation getMathGlobeCalculation(){
        if(this.mathGlobeCalculation == null){
            this.mathGlobeCalculation = new MathGlobeCalculation();
        }
        return this.mathGlobeCalculation;
    }

    private Double distanceDepartureArrival = null;
    public Double getDistanceDepartureArrival(){
        if(this.departure == null || this.arrival == null) return null;
        if(distanceDepartureArrival != null) return distanceDepartureArrival;
        distanceDepartureArrival = getMathGlobeCalculation().getNorm(this.departure, this.arrival);
        return distanceDepartureArrival;

    }

    private Double distanceDepartureToPos = null;
    public Double getDistanceDeparturePOS(){
        if(this.pdv == null || this.pdv.getCoordinate() == null || this.departure == null) return null;
        if(distanceDepartureToPos != null) return distanceDepartureToPos;
        distanceDepartureToPos = getMathGlobeCalculation().getNorm(this.departure, this.pdv.getCoordinate());
        return distanceDepartureToPos;
    }

    private Double distancePOSToArrival = null;
    public Double getDistancePOSArival(){
        if(this.pdv == null || this.pdv.getCoordinate() == null || this.arrival == null) return null;
        if(distancePOSToArrival != null) return distancePOSToArrival;
        distancePOSToArrival = getMathGlobeCalculation().getNorm(this.pdv.getCoordinate(), this.arrival);
        return distancePOSToArrival;
    }

    private Double deltaDistance = null;
    public Double getDistanceDelta(){
        if(getDistanceDeparturePOS() == null) return null;
        if(getDistancePOSArival() == null) return null;
        if(getDistanceDepartureArrival() == null) return null;
        if(deltaDistance != null ) return deltaDistance;
        deltaDistance = getDistanceDeparturePOS() + getDistancePOSArival() - getDistanceDepartureArrival();
        return deltaDistance;
    }
}
