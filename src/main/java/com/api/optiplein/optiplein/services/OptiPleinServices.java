package com.api.optiplein.optiplein.services;

import com.api.optiplein.maths.models.Coordinates;
import com.api.optiplein.maths.models.Middle;
import com.api.optiplein.optiplein.models.responses.*;
import com.api.optiplein.gouv.models.PDV;
import com.api.optiplein.gouv.models.Prix;
import com.api.optiplein.optiplein.models.ComparatorType;
import com.api.optiplein.optiplein.models.OptipleinComparator;
import com.api.optiplein.optiplein.models.requests.PetrolPOSRequest;
import com.api.optiplein.maths.models.Delta;
import com.api.optiplein.maths.services.MathCalculationServices;
import com.api.optiplein.optiplein.models.petroltypes.PetrolType;
import com.api.optiplein.gouv.services.GouvPOSServices;
import com.api.optiplein.gouv.services.GovPOSServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OptiPleinServices {

    @Autowired
    GouvPOSServices gouvPOSServices;

    @Autowired
    MathCalculationServices mathCalculationServices;

    @Autowired
    PDVServices pdvServices;

    public boolean isValidCoordinate(Coordinates coordinates){
        if(coordinates == null) return false;
        if(coordinates.getLatitude() == null || coordinates.getLatitude() > 90.0 || coordinates.getLatitude() < -90.0) return false;
        if(coordinates.getLongitude() == null || coordinates.getLongitude() > 90.0 || coordinates.getLongitude() < -90.0)  return false;
        return true;
    }

    public Map<String, PetrolPOS> findPetrolsPOS(PetrolPOSRequest petrolPOSRequest) throws OptiPleinServiceException, GovPOSServiceException{
        checkPetrolRequest(petrolPOSRequest);


        PetrolType petrolType = getPetrolTypeOnRequest(petrolPOSRequest);
        if(petrolType == null){
            throw new OptiPleinServiceException(("No petrol type found for "+petrolPOSRequest.getPetrolTypes()));
        }

        //Get middle
        Coordinates middleCoordinate = mathCalculationServices.getMiddle(petrolPOSRequest.getDepart(), petrolPOSRequest.getArrive());
        if(middleCoordinate == null){
            throw new OptiPleinServiceException("Cannot calculate middle : null");
        }

        Double normMiddle = mathCalculationServices.getNorm(petrolPOSRequest.getDepart(), middleCoordinate);
        if(normMiddle == null){
            throw new OptiPleinServiceException("Middle norm not calculated");
        }

        Middle middle = new Middle(middleCoordinate,normMiddle);

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        List<PDV> gouvPDVs = gouvPOSServices.loadPDVsJSON(yesterday);


        Map<String, PetrolPOS> petrolPOSMap = new HashMap<>();
        if(gouvPDVs != null){
            Map<String, PetrolPOS> finalPetrolPOSList = petrolPOSMap;
            gouvPDVs.stream().forEach(gouvPDV -> {
                boolean pvdInRange= pdvServices.pdvInRange(middle, gouvPDV, mathCalculationServices);
                boolean pdvHasPetrolTypes = pdvServices.pdvHasPetrolTypes(petrolType, gouvPDV);
                if(pvdInRange && pdvHasPetrolTypes && gouvPDV.getId() != null && !gouvPDV.getId().isEmpty()){
                    Delta delta = new Delta( petrolPOSRequest.getDepart(), petrolPOSRequest.getArrive(), gouvPDV);
                    Double deltaNumber = mathCalculationServices.getDistanceDelta(delta);
                    finalPetrolPOSList.put(gouvPDV.getId(), gouvPDV.toPetrolPos(petrolType, delta, deltaNumber));
                }
            });
            petrolPOSMap = finalPetrolPOSList;
        }
        return petrolPOSMap;
    }

    private void checkPetrolRequest(PetrolPOSRequest petrolPOSRequest) throws OptiPleinServiceException
    {
        if(petrolPOSRequest == null)
            throw new OptiPleinServiceException("No Request");
        if(!isValidCoordinate(petrolPOSRequest.getDepart())){
            throw new OptiPleinServiceException("Wrong departure: null or greater than 90.0 or lower than -90.0");
        }
        if(!isValidCoordinate(petrolPOSRequest.getArrive())){
            throw new OptiPleinServiceException("Wrong arrival: null or greater than 90.0 or lower than -90.0");
        }
        if(petrolPOSRequest.getPetrolTypes() == null || petrolPOSRequest.getPetrolTypes().isEmpty()){
            throw new OptiPleinServiceException("No petrol type!");
        }
    }

    public List<PetrolPOS> getOptimaPetrolPos(Map<String, PetrolPOS> petrolPOSMap, PetrolType petrolType, ComparatorType comparatorType, Long numberOfResult){

        if(petrolPOSMap == null || petrolPOSMap.isEmpty()) return new ArrayList<>();
        if(petrolType == null ) return new ArrayList<>();
        if(numberOfResult == null) return new ArrayList<>();

        //Compare optimum
        OptipleinComparator optiPleinComparatorOptimum = new OptipleinComparator(petrolType, comparatorType);
        List<PetrolPOS> optimumList = new ArrayList<>(petrolPOSMap.values());
        Collections.sort(optimumList, optiPleinComparatorOptimum);

        return optimumList.stream().limit(numberOfResult).collect(Collectors.toList());
    }

    public PetrolType getPetrolTypeOnRequest(PetrolPOSRequest petrolPOSRequest){
        return PetrolType.lookupNames.get(petrolPOSRequest.getPetrolTypes());
    }

    public List<Average> calculateAveragePrice() throws GovPOSServiceException {

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        List<PDV> gouvPDVs = gouvPOSServices.loadPDVsJSON(yesterday);

        Map<PetrolType, BigDecimal> sumByPetrolType = new HashMap<>();
        Map<PetrolType, Integer> countPosByPetrolType = new HashMap<>();

        gouvPDVs.stream().forEach(pdv -> {
            if (pdv != null && pdv.getPetrolIds() != null){
                pdv.getPetrolIds().forEach((k, v) -> {
                    PetrolType petrolType = PetrolType.lookupIds.get(k);
                    Prix prix = v;

                    if (prix != null && prix.getValueDecimal() != null && petrolType != null) {
                        BigDecimal currentSum = sumByPetrolType.get(petrolType);
                        Integer currentCount = countPosByPetrolType.get(petrolType);

                        if (currentSum == null) {
                            currentSum = BigDecimal.ZERO;
                        }
                        currentSum = currentSum.add(prix.getValueDecimal());
                        sumByPetrolType.put(petrolType, currentSum);

                        if (currentCount == null) {
                            currentCount = 0;

                        }
                        currentCount = currentCount + 1;
                        countPosByPetrolType.put(petrolType, currentCount);
                    }
                });
            }
        });

        List<Average> toReturn = new ArrayList<>();
        sumByPetrolType.forEach( (k, v) -> {
            Integer count = countPosByPetrolType.get(k);
            BigDecimal bigDecimalCount = new BigDecimal(count);

            BigDecimal averageAmount = v.divide(bigDecimalCount, 4, RoundingMode.HALF_EVEN);
            Average average = Average.builder()
                    .average(averageAmount)
                    .petrolType(k)
                    .numberOfPos(count)
                    .build();

            toReturn.add(average);
        });
        return toReturn;
    }


    public List<FilterResult> findPetrolPosFilters(PDVFilter filter) throws OptiPleinServiceException, GovPOSServiceException {

        if(filter == null)
            throw new OptiPleinServiceException("No filters");


        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        List<PDV> gouvPDVs = gouvPOSServices.loadPDVsJSON(yesterday);

        List<FilterResult> filteredPVD = new ArrayList<>();
        gouvPDVs.stream().forEach(pdv -> {
             filteredPVD.addAll(filter.toResults(pdv));
        });



        return filteredPVD;
    }

    public PetrolPOSResults getPosForTrip(PetrolPOSRequest petrolPOSRequest) throws OptiPleinServiceException, GovPOSServiceException {
        Map<String, PetrolPOS> petrolPOSMap = findPetrolsPOS(petrolPOSRequest);
        PetrolType petrolType = getPetrolTypeOnRequest(petrolPOSRequest);

        Double distance = mathCalculationServices.getNorm(petrolPOSRequest.getDepart(), petrolPOSRequest.getArrive());


        ResultDetails resultDetails = ResultDetails.builder()
                .departure(petrolPOSRequest.getDepart())
                .arrival(petrolPOSRequest.getArrive())
                .distanceTotal(distance)
                .build();


        Long totalEntity = mathCalculationServices.getRound(distance);
        List<PetrolPOS> optimum = getOptimaPetrolPos(petrolPOSMap, petrolType, ComparatorType.OPTIMUM, totalEntity);
        petrolPOSMap = removePetrolPDVFromMap(petrolPOSMap, optimum);

        List<PetrolPOS> greener = getOptimaPetrolPos(petrolPOSMap, petrolType, ComparatorType.GREENER, totalEntity);
        petrolPOSMap = removePetrolPDVFromMap(petrolPOSMap, greener);

        List<PetrolPOS> cheaper = getOptimaPetrolPos(petrolPOSMap, petrolType, ComparatorType.CHEAPEST, totalEntity);


        PetrolPOSResults results = PetrolPOSResults.builder()
                .optimumPOS(optimum)
                .greenestPOS(greener)
                .cheapestPOS(cheaper)
                .resultDetails(resultDetails)
                .build();

        return results;
    }

    private Map removePetrolPDVFromMap(Map<String, PetrolPOS> petrolPOSMap, List<PetrolPOS> petrolPOS){
        if(petrolPOS != null){
            petrolPOS.forEach(o -> {
                if(o.getId() != null ) petrolPOSMap.remove(o.getId());
            });
        }
        return petrolPOSMap;
    }

}
