package com.api.optiplein.Models.responses;

import com.api.optiplein.Models.PDVFilter;
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
