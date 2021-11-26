package com.api.optiplein.gouv.services;

public enum GovPOSServiceErrors {
    NO_DATE("No date"),
    FILE_ERROR("File error");

    String errorName;
    GovPOSServiceErrors(String error){
        this.errorName = error;
    }
}
