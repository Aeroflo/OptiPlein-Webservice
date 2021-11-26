package com.api.optiplein.gouv.models;

import com.api.optiplein.optiplein.models.responses.Price;
import com.api.optiplein.optiplein.models.petroltypes.PetrolType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.MathContext;

public class Prix {

    @JsonProperty("nom")
    String nom;

    @JsonProperty("id")
    Integer id;

    @JsonProperty("maj")
    String maj;

    @JsonProperty("valeur")
    Integer valeur;

    public Integer getId() {
        return id;
    }

    public BigDecimal getValueDecimal(){
        if(valeur != null){
            Double d = valeur.doubleValue()/1000;
            return new BigDecimal(d, MathContext.DECIMAL64);
        }
        return null;
    }

    public Price toPrice(){
        PetrolType petrolType = PetrolType.lookupIds.get(id);
        return Price.builder()
                .prix(getValueDecimal())
                .type(petrolType)
                .build();
    }



}
