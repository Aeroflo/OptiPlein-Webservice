package com.api.optiplein.optiplein.models.responses;

import com.api.optiplein.optiplein.models.petroltypes.PetrolType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class Average {

    @JsonProperty("petrolType")
    PetrolType petrolType;

    @JsonProperty("averagePrice")
    BigDecimal average;

    @JsonProperty("numberOfPos")
    Integer numberOfPos;
}
