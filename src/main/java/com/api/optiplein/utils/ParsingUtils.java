package com.api.optiplein.utils;

import org.apache.tomcat.util.buf.StringUtils;

public class ParsingUtils {

    public static Double parseDouble(String value){
        if(value == null || value.isEmpty()) return null;
        try{
            Double doubleValue = Double.parseDouble(value);
            return doubleValue;
        }catch (NumberFormatException e){
            return null;
        }
    }

    public static Integer postCodeToInt(String cp){
        if(cp == null || cp.isEmpty()) return null;
        String cpBuffer = cp;
        try {
            if (cp.length() > 2) {
                cpBuffer = cpBuffer.substring(0, 2);
            }
            if (cpBuffer.startsWith("0")) {
                cpBuffer = cpBuffer.substring(1, 2);
            }
            return Integer.parseInt(cpBuffer);
        }
        catch (Exception e){
            return null;
        }

    }
}
