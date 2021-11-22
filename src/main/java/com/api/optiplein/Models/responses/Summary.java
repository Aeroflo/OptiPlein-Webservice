package com.api.optiplein.Models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public class Summary {

    @JsonProperty("average")
    List<Average> averages;
}
