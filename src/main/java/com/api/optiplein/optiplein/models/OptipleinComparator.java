package com.api.optiplein.optiplein.models;

import com.api.optiplein.optiplein.models.ComparatorType;
import com.api.optiplein.optiplein.models.responses.PetrolPOS;
import com.api.optiplein.optiplein.models.responses.Price;
import com.api.optiplein.optiplein.models.petroltypes.PetrolType;

import java.math.BigDecimal;
import java.util.Comparator;

public class OptipleinComparator implements Comparator<PetrolPOS> {

    PetrolType petrolType;
    ComparatorType comparatorType = ComparatorType.OPTIMUM;

    public OptipleinComparator(PetrolType petrolType, ComparatorType comparatorType){
        super();
        this.petrolType = petrolType;
        if(comparatorType != null) this.comparatorType = comparatorType;
    }

    @Override
    public int compare(PetrolPOS petrolPOS1, PetrolPOS petrolPOS2) {
        if(petrolPOS1 == null && petrolPOS2 == null) return 0;
        if(petrolType == null) return 0;



        switch (this.comparatorType){
            case CHEAPEST:
                return compareCheapest(petrolPOS1, petrolPOS2);
            case GREENER:
                return compareGreener(petrolPOS1, petrolPOS2);
            default:
            case OPTIMUM:
                return compareOptimum(petrolPOS1, petrolPOS2);
        }

    }

    private int compareOptimum(PetrolPOS petrolPOS1, PetrolPOS petrolPOS2){
        Price petrolPos1Price = petrolPOS1.getPriceByType(petrolType);
        Price petrolPos2Price = petrolPOS2.getPriceByType(petrolType);
        if(petrolPos1Price == null || petrolPos1Price.getPrix() == null) return -1;
        if(petrolPos2Price == null || petrolPos2Price.getPrix() == null) return 1;


        if(petrolPOS1.getDistanceDelta() == null) return -1;
        if(petrolPOS2.getDistanceDelta() == null) return 1;

        BigDecimal score1 = petrolPos1Price.getPrix().multiply(new BigDecimal(petrolPOS1.getDistanceDelta()));
        BigDecimal score2 = petrolPos2Price.getPrix().multiply((new BigDecimal(petrolPOS2.getDistanceDelta())));
        return score1.compareTo(score2);
    }

    private int compareCheapest(PetrolPOS petrolPOS1, PetrolPOS petrolPOS2){
        Price petrolPos1Price = petrolPOS1.getPriceByType(petrolType);
        Price petrolPos2Price = petrolPOS2.getPriceByType(petrolType);
        if(petrolPos1Price == null || petrolPos1Price.getPrix() == null) return -1;
        if(petrolPos2Price == null || petrolPos2Price.getPrix() == null) return 1;

        return petrolPos1Price.getPrix().compareTo(petrolPos2Price.getPrix());
    }

    private int compareGreener(PetrolPOS petrolPOS1, PetrolPOS petrolPOS2){
        if(petrolPOS1.getDistanceDelta() == null) return -1;
        if(petrolPOS2.getDistanceDelta() == null) return 1;

        return petrolPOS1.getDistanceDelta().compareTo(petrolPOS2.getDistanceDelta());
    }


}
