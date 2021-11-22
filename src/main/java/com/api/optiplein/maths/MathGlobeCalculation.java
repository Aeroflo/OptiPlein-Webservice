package com.api.optiplein.maths;

import com.api.optiplein.Models.Coordinates;

import java.util.Map;

public class MathGlobeCalculation {

    private static final Integer R = 6371;

    public Coordinates getMiddle(Coordinates coordinatesA, Coordinates coordinatesB){
        if(coordinatesA == null || coordinatesA.getLatitude() == null || coordinatesA.getLongitude() == null)
            return null;
        if(coordinatesB == null || coordinatesB.getLatitude() == null || coordinatesB.getLongitude() == null)
            return  null;


        Double latitude1 = Math.toRadians(coordinatesA.getLatitude());
        Double latitude2 = Math.toRadians(coordinatesB.getLatitude());
        Double longitude1 = Math.toRadians(coordinatesA.getLongitude());
        Double deltaLongitude = Math.toRadians(coordinatesB.getLongitude() - coordinatesA.getLongitude());

        Double bx = Math.cos(latitude2) * Math.cos(deltaLongitude);
        Double by = Math.cos(latitude2) * Math.sin(deltaLongitude);

        Double latitude3 = Math.atan2(
                Math.sin(latitude1) + Math.sin(latitude2),
                Math.sqrt((Math.cos(latitude1) + bx) * (Math.cos(latitude1) + bx) + by * by));

        Double longitude3 = longitude1 + Math.atan2(by, Math.cos(latitude1) + bx);

        return Coordinates.builder()
                .latitude(Math.toDegrees(latitude3))
                .longitude(Math.toDegrees(longitude3))
                .build();
    }

    public Double getNorm(Coordinates coordinatesA, Coordinates coordinatesB){
        if(coordinatesA == null || coordinatesA.getLatitude() == null || coordinatesA.getLongitude() == null)
            return null;
        if(coordinatesB == null || coordinatesB.getLatitude() == null || coordinatesB.getLongitude() == null)
            return  null;

        Double latitudeDistance = Math.toRadians(coordinatesA.getLatitude() - coordinatesB.getLatitude());
        Double longitudeDistance = Math.toRadians(coordinatesA.getLongitude() - coordinatesB.getLongitude());

        Double latitude1  = Math.toRadians(coordinatesA.getLatitude());
        Double latitude2 = Math.toRadians(coordinatesB.getLatitude());

        Double a = Math.sin(latitudeDistance/2) * Math.sin(latitudeDistance/2)
                + Math.cos(latitude1) * Math.cos(latitude2) * Math.sin(longitudeDistance /2) * Math.sin(longitudeDistance/2);

        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 -a));
        Double norm = R * c;
        return norm;
    }
}
