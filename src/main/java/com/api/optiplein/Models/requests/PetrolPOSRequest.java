package com.api.optiplein.Models.requests;

import com.api.optiplein.Models.Coordinates;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PetrolPOSRequest {

    @JsonProperty("departure")
    Coordinates depart;

    @JsonProperty("arrival")
    Coordinates arrive;

    @JsonProperty("petrolTypes")
    String petrolTypes; //Optional???

    public Coordinates getDepart() {
        return depart;
    }

    public Coordinates getArrive() {
        return arrive;
    }

    public String getPetrolTypes() {
        return petrolTypes;
    }
}
