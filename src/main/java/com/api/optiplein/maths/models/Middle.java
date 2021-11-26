package com.api.optiplein.maths.models;

import com.api.optiplein.maths.models.Coordinates;

public class Middle {
    private Coordinates middle;
    private Double norm;

    public Middle(Coordinates middle, Double norm){
        this.middle = middle;
        this.norm = norm;
    }

    public boolean isValid(){
        return middle != null && norm != null;
    }

    public Coordinates getMiddle() {
        return middle;
    }

    public Double getNorm() {
        return norm;
    }
}
