package com.api.optiplein.services.optipleinServices;

import com.api.optiplein.Models.Coordinates;
import com.api.optiplein.Models.PDVFilter;
import com.api.optiplein.Models.preparation.PDV;
import com.api.optiplein.Models.preparation.Prix;
import com.api.optiplein.Models.requests.PetrolPOSRequest;
import com.api.optiplein.Models.responses.Average;
import com.api.optiplein.Models.responses.FilterResult;
import com.api.optiplein.Models.responses.PetrolPOS;
import com.api.optiplein.Models.responses.Price;
import com.api.optiplein.maths.MathGlobeCalculation;
import com.api.optiplein.petrolTypes.PetrolType;
import com.api.optiplein.petrolTypes.PetrolTypeException;
import com.api.optiplein.services.gouvServices.GouvPOSServices;
import com.api.optiplein.services.gouvServices.GovPOSServiceException;
import com.api.optiplein.utils.ParsingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OptiPleinServices {

    @Autowired
    GouvPOSServices gouvPOSServices;

    public boolean isValidCoordinate(Coordinates coordinates){
        if(coordinates == null) return false;
        if(coordinates.getLatitude() == null || coordinates.getLatitude() > 90.0 || coordinates.getLatitude() < -90.0) return false;
        if(coordinates.getLongitude() == null || coordinates.getLongitude() > 90.0 || coordinates.getLongitude() < -90.0)  return false;
        return true;
    }

    public Map<Integer, PetrolType> getPetrolTypesFromString (List<String> petrolTypes) throws PetrolTypeException{
        if(petrolTypes == null || petrolTypes.isEmpty()) return PetrolType.lookupIds;

        AtomicBoolean isValid = new AtomicBoolean(true);
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());

        Map<Integer, PetrolType> petrolTypeMap = new HashMap<>();
        petrolTypes.stream().forEach(name -> {
            PetrolType currentPetrolType = PetrolType.lookupNames.get(name.toUpperCase());
            if(currentPetrolType != null){
                petrolTypeMap.put(currentPetrolType.getId(), currentPetrolType);
            }
            else{
                isValid.set(false);
                message.set(message.get().append(name).append(", "));
            }
        });
        if(!isValid.get()) {
            throw new PetrolTypeException("Invalid petrol types ["+message.get().toString()+"]");
        }
        return petrolTypeMap;
    }

    public Map<String, PetrolPOS> findPetrolsPOS(PetrolPOSRequest petrolPOSRequest) throws OptiPleinServiceException, GovPOSServiceException{
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

        PetrolType petrolType = getPetrolTypeOnRequest(petrolPOSRequest);
        if(petrolType == null){
            throw new OptiPleinServiceException(("No petrol type found for "+petrolPOSRequest.getPetrolTypes()));
        }


        //Get middle
        MathGlobeCalculation mathGlobeCalculation = new MathGlobeCalculation();
        Coordinates middleCoordinate = mathGlobeCalculation.getMiddle(petrolPOSRequest.getDepart(), petrolPOSRequest.getArrive());
        if(middleCoordinate == null){
            throw new OptiPleinServiceException("Cannot calculate middle : null");
        }

        Double normMiddle = mathGlobeCalculation.getNorm(petrolPOSRequest.getDepart(), middleCoordinate);
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
                if(pdvInRange(middle, gouvPDV, mathGlobeCalculation) && pdvHasPetrolTypes(petrolType, gouvPDV) && gouvPDV.getId() != null && !gouvPDV.getId().isEmpty()){
                    DeltaCalculation deltaCalculation = new DeltaCalculation(middleCoordinate, petrolPOSRequest.getDepart(), petrolPOSRequest.getArrive(), gouvPDV);
                    finalPetrolPOSList.put(gouvPDV.getId(), gouvPDV.toPetrolPos(petrolType, deltaCalculation));
                }
            });
            petrolPOSMap = finalPetrolPOSList;
        }


        return petrolPOSMap;
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

    private void printPetrolsList(List<PetrolPOS> petrolPOSList){
        if(petrolPOSList == null) return;
        petrolPOSList.forEach(petrolPos -> {
            StringBuilder stringBuilder = new StringBuilder(petrolPos.getId());
            stringBuilder = stringBuilder.append(" ").append(petrolPos.getVille()).append(" - ")
                    .append(petrolPos.getDistanceDelta()).append(" : ");

            AtomicReference<StringBuilder> pricesString = new AtomicReference<>(new StringBuilder());
            if(petrolPos.getPrice() != null){
                petrolPos.getPrice().forEach(price -> {
                    pricesString.set(pricesString.get().append(price.getType()).append("=").append(price.getPrix()));
                });
            }
            stringBuilder = stringBuilder.append(pricesString.get().toString());
            System.out.println(stringBuilder.toString());
        });
    }



    private static class Middle{
        Coordinates middle;
        Double norm;

        Middle(Coordinates middle, Double norm){
            this.middle = middle;
            this.norm = norm;
        }

        protected boolean isValid(){
            return middle != null && norm != null;
        }
    }


    private boolean pdvInRange(Middle middle, PDV pdv, MathGlobeCalculation mathGlobeCalculation){
            if(!middle.isValid()) return false;
            if(pdv.getCoordinate() == null) return false;

            Double norm = mathGlobeCalculation.getNorm(middle.middle, pdv.getCoordinate());
            if(norm < middle.norm) return true;
            else return false;
    }

    private boolean pdvHasPetrolTypes(PetrolType petrolType, PDV pdv){
        if(petrolType == null) return false;
        if(pdv.getPetrolIds() == null || pdv.getPetrolIds().isEmpty()) return false;
        AtomicBoolean hasPetrolType = new AtomicBoolean(false);
        pdv.getPetrolIds().keySet().stream().forEach(petrolId -> {
            if(petrolId == petrolType.getId()) hasPetrolType.set(true);
        });
        return hasPetrolType.get();
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

}
