package com.api.optiplein.optiplein.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class FilterResult {

    @JsonProperty("id")
    String pdvId;

    @JsonProperty("postalCode")
    String postalCode;

    @JsonProperty("city")
    String city;

    @JsonProperty("address")
    String address;

    @JsonProperty("petrolType")
    String petrolType;

    @JsonProperty("price")
    BigDecimal price;

    @JsonProperty("distance")
    Double distance;
}
