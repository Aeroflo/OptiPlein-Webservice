package Utils;

import java.time.LocalDateTime;

public class Utils {

    public static boolean estDansLaZone(double latitudeCentre, double longitudeCentre, double latitude, double longitude, double perimetre ){
        double total = Math.pow(latitude - latitudeCentre, 2) + Math.pow(longitude - longitudeCentre, 2);
        if(total < Math.pow(perimetre, 2)) return true;
        return false;
    }

    public static Double parseCoordinate(String coordinate){
        if(coordinate == null || coordinate.isEmpty()) return null;
        else{
            try{
                Double d = Double.parseDouble(coordinate);
                return d/10000;
            }catch (NumberFormatException e){
                return null;
            }
        }
    }
}
