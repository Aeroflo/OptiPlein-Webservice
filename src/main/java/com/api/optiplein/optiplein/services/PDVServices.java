package com.api.optiplein.optiplein.services;


import com.api.optiplein.maths.services.MathCalculationServices;
import com.api.optiplein.maths.models.Middle;
import com.api.optiplein.gouv.models.PDV;
import com.api.optiplein.optiplein.models.petroltypes.PetrolType;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PDVServices {

    public boolean pdvHasPetrolTypes(PetrolType petrolType, PDV pdv){
        if(petrolType == null) return false;
        if(pdv.getPetrolIds() == null || pdv.getPetrolIds().isEmpty()) return false;
        AtomicBoolean hasPetrolType = new AtomicBoolean(false);
        pdv.getPetrolIds().keySet().stream().forEach(petrolId -> {
            if(petrolId == petrolType.getId()) hasPetrolType.set(true);
        });
        return hasPetrolType.get();
    }

    public boolean pdvInRange(Middle middle, PDV pdv, MathCalculationServices mathCalculationServices){
        if(!middle.isValid()) return false;
        if(pdv.getCoordinate() == null) return false;

        Double norm = mathCalculationServices.getNorm(middle.getMiddle(), pdv.getCoordinate());
        if(norm < middle.getNorm()) return true;
        else return false;
    }
}
