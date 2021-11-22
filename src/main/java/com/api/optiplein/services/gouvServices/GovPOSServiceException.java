package com.api.optiplein.services.gouvServices;

import com.api.optiplein.services.gouvServices.GovPOSServiceErrors;

public class GovPOSServiceException extends Exception{

    GovPOSServiceErrors govPOSServiceErrors;

    GovPOSServiceException(String message){
        super(message);
    }

    GovPOSServiceException(GovPOSServiceErrors error){
        this.govPOSServiceErrors = error;
    }


}
