package com.api.optiplein.Models.responses;


import com.api.optiplein.petrolTypes.PetrolType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;
import java.util.Map;

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
