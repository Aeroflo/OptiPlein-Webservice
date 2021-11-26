package com.api.optiplein.optiplein.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public class Summary {

    @JsonProperty("average")
    List<Average> averages;
}
