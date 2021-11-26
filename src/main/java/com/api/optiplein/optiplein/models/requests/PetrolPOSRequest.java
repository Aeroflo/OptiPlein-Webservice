package com.api.optiplein.optiplein.models.requests;

import com.api.optiplein.maths.models.Coordinates;
import com.fasterxml.jackson.annotation.JsonProperty;

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
