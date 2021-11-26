package com.api.optiplein.optiplein.controllers;

import com.api.optiplein.optiplein.models.responses.PDVFilter;
import com.api.optiplein.optiplein.models.requests.PetrolPOSRequest;
import com.api.optiplein.maths.services.MathCalculationServices;
import com.api.optiplein.optiplein.models.responses.*;
import com.api.optiplein.optiplein.models.petroltypes.PetrolType;
import com.api.optiplein.gouv.services.GovPOSServiceException;
import com.api.optiplein.optiplein.models.ComparatorType;
import com.api.optiplein.optiplein.services.OptiPleinServiceException;
import com.api.optiplein.optiplein.services.OptiPleinServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class OptipleinController {

    @Autowired
    OptiPleinServices optiPleinServices;


    @PostMapping(value ="/")
    public ResponseEntity getPOSForTrip(@RequestBody PetrolPOSRequest petrolPOSRequest){

        try {

            PetrolPOSResults results = optiPleinServices.getPosForTrip(petrolPOSRequest);
            return new ResponseEntity<PetrolPOSResults>(results, HttpStatus.OK);

        } catch (OptiPleinServiceException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (GovPOSServiceException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value ="/petrolTypes")
    public ResponseEntity getPetrolTypes(){
        return new ResponseEntity(PetrolType.values(), HttpStatus.OK);
    }

    @GetMapping(value = "/summary")
    public ResponseEntity getSummary(){
        Summary.SummaryBuilder summaryBuilder = Summary.builder();
        try {
            List<Average> averages = optiPleinServices.calculateAveragePrice();
            summaryBuilder.averages(averages);
        } catch (GovPOSServiceException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Summary>(summaryBuilder.build(), HttpStatus.OK);
    }

    @PostMapping(value = "/filter")
    public ResponseEntity getPOSFiltered(@RequestBody PDVFilter filter){

        try {
            List<FilterResult> results = optiPleinServices.findPetrolPosFilters(filter);
            FilterResults filterResults = FilterResults.builder()
                    .filters(filter)
                    .results(results)
                    .build();
            return new ResponseEntity(filterResults, HttpStatus.OK);
        } catch (OptiPleinServiceException e) {
            e.printStackTrace();
        } catch (GovPOSServiceException e) {
            e.printStackTrace();
        }
        return null;
    }

}
