package com.api.optiplein.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class Coordinates {

    @JsonProperty("latitude")
    Double latitude;

    @JsonProperty("longitude")
    Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
