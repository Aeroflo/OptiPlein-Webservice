package com.api.optiplein.gouv.models;

import com.api.optiplein.maths.models.Coordinates;
import com.api.optiplein.optiplein.models.responses.PetrolPOS;
import com.api.optiplein.optiplein.models.responses.Price;
import com.api.optiplein.optiplein.models.petroltypes.PetrolType;
import com.api.optiplein.maths.models.Delta;
import com.api.optiplein.utils.ParsingUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;


public class PDV {

    @JsonProperty("id")
    String id;

    @JsonProperty("latitude")
    String latitude;

    @JsonProperty("longitude")
    String longitude;

    @JsonProperty("cp")
    String cp;

    @JsonProperty("adresse")
    String adresse;

    @JsonProperty("ville")
    String ville;

    //Services

    @JsonProperty("prix")
    List<Prix> prix;

    public Coordinates getCoordinate(){
        if(this.longitude == null || this.latitude == null) return null;

        Double latDouble = ParsingUtils.parseDouble(this.latitude);
        Double longDouble = ParsingUtils.parseDouble(this.longitude);
        if(latDouble == null || longDouble == null) return null;
        else return Coordinates.builder()
                .latitude(latDouble/100000.0)
                .longitude(longDouble/100000.0)
                .build();
    }

    private Map<Integer, Prix> petrolTypesIds = null;
    public Map<Integer, Prix> getPetrolIds(){
        if(this.prix != null && this.petrolTypesIds == null) {
            Map<Integer, Prix> currentPetrolTypesIds = new HashMap<>();
            this.prix.stream().forEach(prix1 -> {
                if(prix1.id != null) currentPetrolTypesIds.put(prix1.id, prix1);
            });
            this.petrolTypesIds = currentPetrolTypesIds;
        }
        return petrolTypesIds;
    }

    private List<Prix> getPrixSorted(PetrolType petrolType){
        if(petrolType == null) return new ArrayList<>();
        if(this.getPetrolIds() == null || this.getPetrolIds().isEmpty()) return new ArrayList<>();

        //Todo review this : not clear
        List<Prix> toReturn = new ArrayList<>();

        if(this.prix!= null){
            this.prix.forEach(p -> {
                if(p.getId() != null && p.getId().equals(petrolType.getId())){
                    toReturn.add(p);
                }
            });
        }
        return toReturn;
    }

    public PetrolPOS toPetrolPos(PetrolType petrolType, Delta delta, Double deltaNumber){
        PetrolPOS.PetrolPOSBuilder builder = PetrolPOS.builder();
        builder.id(this.id)
                .coordinates(this.getCoordinate())
                .address(this.adresse)
                .ville(this.ville)
                .distanceDelta(deltaNumber)
                ;

        List<Price> prices = new ArrayList<>();
        List<Prix> prix = getPrixSorted(petrolType);
        prix.forEach(p -> {
            Price price = p.toPrice();
            prices.add(price);
        });

        builder.price(prices);
        return builder.build();
    }

    public String getId() {
        return id;
    }

    public String getPostCode(){return cp;}

    public String getVille(){return this.ville;}

    public String getAdresse(){return this.adresse;}
}
