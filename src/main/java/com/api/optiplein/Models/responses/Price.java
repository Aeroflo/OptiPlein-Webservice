package com.api.optiplein.Models.responses;

import com.api.optiplein.petrolTypes.PetrolType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class Price {

    @JsonProperty("type")
    PetrolType type;

    @JsonProperty("price")
    BigDecimal prix;

    public PetrolType getType() {
        return type;
    }

    public BigDecimal getPrix() {
        return prix;
    }
}
