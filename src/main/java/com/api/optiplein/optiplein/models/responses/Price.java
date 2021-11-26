package com.api.optiplein.optiplein.models.responses;

import com.api.optiplein.optiplein.models.petroltypes.PetrolType;
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
