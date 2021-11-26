package com.api.optiplein.optiplein.models.responses;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public class PetrolPOSResults {

    @JsonProperty("resultsDetails")
    ResultDetails resultDetails;

    @JsonProperty("optimumPOS")
    List<PetrolPOS> optimumPOS;

    @JsonProperty("cheepestPOS")
    List<PetrolPOS> cheapestPOS;

    @JsonProperty("greenestPOS")
    List<PetrolPOS> greenestPOS;
}
