package com.api.optiplein.Models.responses;

import com.api.optiplein.Models.Coordinates;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class ResultDetails {

    @JsonProperty("departure")
    Coordinates departure;

    @JsonProperty("arrival")
    Coordinates arrival;

    @JsonProperty("distanceTotal")
    Double distanceTotal;
}
