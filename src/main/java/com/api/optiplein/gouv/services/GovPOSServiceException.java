package com.api.optiplein.gouv.services;

import com.api.optiplein.gouv.services.GovPOSServiceErrors;

public class GovPOSServiceException extends Exception{

    GovPOSServiceErrors govPOSServiceErrors;

    GovPOSServiceException(String message){
        super(message);
    }

    GovPOSServiceException(GovPOSServiceErrors error){
        this.govPOSServiceErrors = error;
    }


}
