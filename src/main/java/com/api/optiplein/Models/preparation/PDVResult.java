package com.api.optiplein.Models.preparation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class PDVResult {
    @JsonProperty("pdv")
    List<PDV> pdv;

    public List<PDV> getPdv() {
        return pdv;
    }
}
