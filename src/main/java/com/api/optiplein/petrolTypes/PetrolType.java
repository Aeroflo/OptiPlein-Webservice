package com.api.optiplein.petrolTypes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum PetrolType {
    GAZOLE(1, "Gazole"),
    SP95(2, "Sans Plomb 95 (SP95)"),
    E85(3, "Super Ethanol (E85)"),
    GPL(4, "GPL") ,
    E10(5, "Sans Plomb 95 E10 (E10)"),
    SP98(6, "Sans Plomb 98 (SP98)")
    ;

    private int id;
    public Integer getId(){return this.id;}

    private String fullName;

    PetrolType(int id, String fullName){
        this.id = id;
        this.fullName = fullName;
    }

    public static Map<String, PetrolType> lookupNames = new HashMap<>();
    public static Map<Integer, PetrolType> lookupIds = new HashMap<>();

    static{
        Arrays.stream(PetrolType.values()).forEach( petrolType -> {
            lookupNames.put(petrolType.name(), petrolType);
            lookupIds.put(petrolType.getId(), petrolType);
        });
    }





}
