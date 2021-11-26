package com.api.optiplein.optiplein.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public class FilterResults {

    @JsonProperty("filters")
    PDVFilter filters;

    @JsonProperty("results")
    List<FilterResult> results;
}
