package com.api.optiplein.utils;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateUtils {

    public static final String LOCAL_DATE_YYYY_MM_DD = "yyyy_MM_dd";
    public static final String LOCAL_DATE_YYYYMMDD = "yyyyMMdd";

    public static String localDateToStringFormat(LocalDate localDate, String pattern){
        if(localDate == null || pattern == null) return null;
        else{
            try{
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
                String formattedResult = localDate.format(dateTimeFormatter);
                return formattedResult;
            }catch (Exception e){
                return null;
            }
        }
    }
}
