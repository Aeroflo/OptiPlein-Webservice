package com.api.optiplein.Models.responses;

import com.api.optiplein.petrolTypes.PetrolType;
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
