package com.api.optiplein.Models.responses;

import com.api.optiplein.Models.Coordinates;
import com.api.optiplein.petrolTypes.PetrolType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Builder
public class PetrolPOS {

    @JsonProperty("id")
    String id;

    @JsonProperty("coordinate")
    Coordinates coordinates;

    @JsonProperty("address")
    String address;

    @JsonProperty("town")
    String ville;

    @JsonProperty("prices") //to change
    List<Price> price;

    @JsonProperty("fromDepartureToPOS")
    Double distanceDeparturePOS;

    @JsonProperty("fromPOSToArrival")
    Double distancePOSArrival;

    //Delta distance
    @JsonProperty("distanceDelta")
    Double distanceDelta;

    public Price getPriceByType(PetrolType petrolType){
        if(petrolType == null || this.price == null || this.price.isEmpty()) return null;
        Price priceToReturn = null;
        Iterator<Price> iterator = price.iterator();

        while(priceToReturn == null && iterator.hasNext()){
            Price currentPrice = iterator.next();
            if(currentPrice.type != null && currentPrice.type.equals(petrolType)){
                priceToReturn = currentPrice;
            }
        }
        return priceToReturn;
    }

    public List<Price> getPrice() {
        return price;
    }


    public String getId() {
        return id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getAddress() {
        return address;
    }

    public String getVille() {
        return ville;
    }

    public Double getDistanceDeparturePOS() {
        return distanceDeparturePOS;
    }

    public Double getDistancePOSArrival() {
        return distancePOSArrival;
    }

    public Double getDistanceDelta() {
        return distanceDelta;
    }
}
