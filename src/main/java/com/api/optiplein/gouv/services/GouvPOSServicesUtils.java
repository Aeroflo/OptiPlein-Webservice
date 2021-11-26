package com.api.optiplein.gouv.services;

import com.api.optiplein.gouv.models.PDV;
import com.api.optiplein.gouv.models.PDVResult;
import com.api.optiplein.utils.LocalDateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.boot.json.GsonJsonParser;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GouvPOSServicesUtils {

    protected static final String LOCAL_FOLDER = "C:\\Users\\Flo\\Documents\\petrolPDVs\\";
    protected static final String XML_FILE_PREFIX ="PrixCarburants_quotidien_";

    protected enum FileType{
        ZIP(".zip"),
        XML(".xml"),
        JSON(".json");

        String extention;
        FileType(String extension ){
            this.extention = extension;
        }
    }

    /*
    NOTES:
     downloaded file        ROOT/yyyy_MM_dd.zip
     xml file unzip         ROOT/PrixCarburant_quotidien_yyyyMMdd.xml
     json file generated    ROOT/yyyy_MM_dd/PDVs.json
     */


    public static File getJsonFileForDate(LocalDate localDate) throws GovPOSServiceException {
        if(localDate == null ) throw new GovPOSServiceException(GovPOSServiceErrors.NO_DATE);
        return buildFilePath(localDate, FileType.JSON);
    }

    public static File getXMLFileForDate(LocalDate localDate) throws GovPOSServiceException{
        if(localDate == null ) throw new GovPOSServiceException(GovPOSServiceErrors.NO_DATE);
        return buildFilePath(localDate, FileType.XML);
    }

    public static File getZipFileForDate(LocalDate localDate) throws GovPOSServiceException{
        if(localDate == null ) throw new GovPOSServiceException(GovPOSServiceErrors.NO_DATE);
        return buildFilePath(localDate, FileType.ZIP);
    }

    protected static File buildFilePath(LocalDate localDate, FileType fileType) throws GovPOSServiceException{
        if(localDate == null ) throw new GovPOSServiceException(GovPOSServiceErrors.NO_DATE);
        if(fileType == null ) throw  new GovPOSServiceException(GovPOSServiceErrors.FILE_ERROR);

        StringBuilder file = new StringBuilder(LOCAL_FOLDER);
        switch (fileType){
            case JSON:
                String folder = LocalDateUtils.localDateToStringFormat(localDate, LocalDateUtils.LOCAL_DATE_YYYYMMDD);
                file = file.append(folder).append("/");
                break;
            case XML:
                String localDateFile = LocalDateUtils.localDateToStringFormat(localDate, LocalDateUtils.LOCAL_DATE_YYYYMMDD);
                file = file.append(XML_FILE_PREFIX).append(localDateFile).append(fileType.extention);
                break;
            case ZIP:
                folder = LocalDateUtils.localDateToStringFormat(localDate, LocalDateUtils.LOCAL_DATE_YYYY_MM_DD);
                file = file.append(folder).append(fileType.extention);
                break;
            default:
                throw new GovPOSServiceException(GovPOSServiceErrors.FILE_ERROR);
        }

        return new File(file.toString());
    }

    protected static File writeInJsonFile(String values, LocalDate localDate) throws GovPOSServiceException{
        File jsonFile = getJsonFileForDate(localDate);
        FileWriter writer = null;
        try {
            writer = new FileWriter(jsonFile);
            writer.write(values);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } finally {
            if(writer != null) try{ writer.close();} catch (Exception e) {}
        }
        return jsonFile;
    }

    protected static List<PDV> getPDVFromJsonFile(File jsonFile){
        GsonJsonParser gsonJsonParser = new GsonJsonParser();
        Reader reader = null;
        PDVResult pdvs = null;
        try {
            reader = new FileReader(jsonFile);
            com.google.gson.Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<PDV>>(){}.getType();
            pdvs = gson.fromJson(reader, PDVResult.class);
            System.out.println();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
            if(reader != null) try{reader.close();}catch(Exception e){}
        }
        return pdvs != null ? pdvs.getPdv() : new ArrayList<>();
    }

}
