package com.api.optiplein.optiplein.models.responses;


import com.api.optiplein.gouv.models.PDV;
import com.api.optiplein.maths.models.Coordinates;
import com.api.optiplein.optiplein.models.responses.FilterResult;
import com.api.optiplein.maths.services.MathCalculationServices;
import com.api.optiplein.optiplein.models.petroltypes.PetrolType;
import com.api.optiplein.utils.ParsingUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PDVFilter {

    @JsonProperty("county")
    Integer county;

    @JsonProperty("postalCode")
    String postalCode;

    @JsonProperty("city")
    String city;

    @JsonProperty("petrolTypes")
    List<String> petrolTypes;

    @JsonProperty("position")
    Coordinates position;

    private Set<Integer> petrolTypesEnum;


    private transient MathCalculationServices mathCalculationServices;
    private MathCalculationServices getMathGlobeCalculation(){
        if(this.mathCalculationServices == null){
            this.mathCalculationServices = new MathCalculationServices();
        }
        return this.mathCalculationServices;
    }

    private Set<Integer> getAllPetrolTypesEnum(){
        if(this.petrolTypesEnum == null && petrolTypes != null && !petrolTypes.isEmpty()){
            Set<Integer> buffer = new HashSet<>();
            petrolTypes.forEach(p -> {
                PetrolType petrolType = PetrolType.lookupNames.get(p);
                if(petrolType != null){
                    buffer.add(petrolType.getId());
                }
            });
            this.petrolTypesEnum = buffer;
        }
        else if(this.petrolTypesEnum == null){
            this.petrolTypesEnum = PetrolType.lookupIds.keySet();
        }
        return this.petrolTypesEnum;
    }

    private boolean pdvMatches(PDV pdv){
        if(pdv == null) return false;

        Integer pdvPostCode = ParsingUtils.postCodeToInt(pdv.getPostCode());
        if(this.county != null && !this.county.equals(pdvPostCode))
            return false;

        if(postalCode != null && !postalCode.isEmpty() && !postalCode.equals(pdv.getPostCode()))
            return false;

        if(city != null && !city.isEmpty() && pdv.getVille() != null && !city.contains(pdv.getVille()))
            return false;

        return true;
    }

    public List<FilterResult> toResults(PDV pdv){
        List<FilterResult> toReturn = new ArrayList<>();
        if(!pdvMatches(pdv)) return toReturn;


        if(pdv.getPetrolIds() != null){
            Double distance = this.getMathGlobeCalculation().getNorm(this.position, pdv.getCoordinate());
            if(distance != null){
                BigDecimal bd = new BigDecimal(distance);
                
                distance = bd.setScale(1, RoundingMode.HALF_UP).doubleValue();
            }
            Double finalDistance = distance;
            pdv.getPetrolIds().forEach((k, v) ->{
                if(getAllPetrolTypesEnum().contains(k)){
                    PetrolType currentPetrolType = PetrolType.lookupIds.get(k);
                    FilterResult filterResult = FilterResult.builder()
                            .pdvId(pdv.getId())
                            .postalCode(pdv.getPostCode())
                            .city(pdv.getVille())
                            .address(pdv.getAdresse())
                            .petrolType(currentPetrolType.name())
                            .price(v.getValueDecimal())
                            .distance(finalDistance)
                            .build();
                    toReturn.add(filterResult);
                }
            });
        }
        return toReturn;
    }


}
