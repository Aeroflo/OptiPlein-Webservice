package com.api.optiplein.maths.models;

import com.api.optiplein.maths.services.MathCalculationServices;
import com.api.optiplein.gouv.models.PDV;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
public class Delta {

    private Coordinates departure;
    private Coordinates arrival;
    private PDV pdv;
    private double delta;


    public Delta(Coordinates departure, Coordinates arrival, PDV pdv){
        this.departure = departure;
        this.arrival = arrival;
        this.pdv = pdv;
    }
}
