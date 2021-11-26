package com.api.optiplein.optiplein.models.responses;

import com.api.optiplein.maths.models.Coordinates;
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
