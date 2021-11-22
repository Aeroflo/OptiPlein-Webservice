package com.api.optiplein.controllers;

import com.api.optiplein.Models.Coordinates;
import com.api.optiplein.Models.PDVFilter;
import com.api.optiplein.Models.preparation.PDV;
import com.api.optiplein.Models.requests.PetrolPOSRequest;
import com.api.optiplein.Models.responses.*;
import com.api.optiplein.maths.MathGlobeCalculation;
import com.api.optiplein.petrolTypes.PetrolType;
import com.api.optiplein.petrolTypes.PetrolTypeException;
import com.api.optiplein.services.gouvServices.GouvPOSServices;
import com.api.optiplein.services.gouvServices.GovPOSServiceException;
import com.api.optiplein.services.optipleinServices.ComparatorType;
import com.api.optiplein.services.optipleinServices.OptiPleinServiceException;
import com.api.optiplein.services.optipleinServices.OptiPleinServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class OptipleinController {

    @Autowired
    GouvPOSServices  gouvPOSServices;

    @Autowired
    OptiPleinServices optiPleinServices;

    @GetMapping("/hello")
    public ResponseEntity hello(){
        return new ResponseEntity("{\"h\" = \"hello\"}", HttpStatus.OK);
    }

    @GetMapping(value = "/")
    public ResponseEntity getOptiPleinTitle(){
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        try{
           gouvPOSServices.loadPDVsJSON(yesterday);
        }catch (GovPOSServiceException govPOSServiceException){

        }
        //gouvPOSServices.loadPDVsJSON();
        return new ResponseEntity("Hello Optiplein", HttpStatus.OK);
    }

    @PostMapping(value ="/")
    public ResponseEntity getPOSForTrip(@RequestBody PetrolPOSRequest petrolPOSRequest){

        try {
            Map<String, PetrolPOS> petrolPOSMap = optiPleinServices.findPetrolsPOS(petrolPOSRequest);

            PetrolType petrolType = optiPleinServices.getPetrolTypeOnRequest(petrolPOSRequest);

            MathGlobeCalculation mathGlobeCalculation = new MathGlobeCalculation();
            Double distance = mathGlobeCalculation.getNorm(petrolPOSRequest.getDepart(), petrolPOSRequest.getArrive());


            ResultDetails resultDetails = ResultDetails.builder()
                    .departure(petrolPOSRequest.getDepart())
                    .arrival(petrolPOSRequest.getArrive())
                    .distanceTotal(distance)
                    .build();

            Double numberToDisplay = Math.log(distance) * Math.log(5);
            Double numberToDisplayBy2 = numberToDisplay /2;

            Long round = Math.round(numberToDisplay);
            Long roundBy2 = Math.round(numberToDisplayBy2);
            if(round < 1) round = 1l;
            if(roundBy2 < 1) roundBy2 = 1l;

            List<PetrolPOS> greener = optiPleinServices.getOptimaPetrolPos(petrolPOSMap, petrolType, ComparatorType.GREENER, roundBy2);
            if(greener != null){
                greener.forEach(o -> {
                    if(o.getId() != null ) petrolPOSMap.remove(o.getId());
                });
            }

            List<PetrolPOS> optimum = optiPleinServices.getOptimaPetrolPos(petrolPOSMap, petrolType, ComparatorType.OPTIMUM, round);
            if(optimum != null){
                optimum.forEach(o -> {
                    if(o.getId() != null ) petrolPOSMap.remove(o.getId());
                });
            }

            List<PetrolPOS> cheaper = optiPleinServices.getOptimaPetrolPos(petrolPOSMap, petrolType, ComparatorType.CHEAPEST, roundBy2);


            PetrolPOSResults results = PetrolPOSResults.builder()
                    .optimumPOS(optimum)
                    .greenestPOS(greener)
                    .cheapestPOS(cheaper)
                    .resultDetails(resultDetails)
                    .build();

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
