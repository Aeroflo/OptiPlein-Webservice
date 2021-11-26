package com.api.optiplein.gouv.services;

import com.api.optiplein.gouv.models.PDV;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class GouvPOSServices {

    private static final String ENDPOINT = "https://donnees.roulez-eco.fr/";
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int DEFAULT_BUFFER_SIZE = 8192;


    public List<PDV> loadPDVsJSON(LocalDate localDate) throws GovPOSServiceException {
        File jsonFile = GouvPOSServicesUtils.getJsonFileForDate(localDate);
        File xmlFile = GouvPOSServicesUtils.getXMLFileForDate(localDate);
        File zipFile = GouvPOSServicesUtils.getZipFileForDate(localDate);

        if(!jsonFile.exists()){
            System.out.println("NO JSON FILE");
            if(!xmlFile.exists()) {
                System.out.println("NO XML FILE");
                if (!zipFile.exists()) {
                    System.out.println("NO ZIP FILE");
                    zipFile = downloadPDVFile(localDate);
                }
                unzipFile(zipFile, localDate);
                if(!xmlFile.exists()){
                    System.out.println("STILL NO XML FILE");
                    throw new GovPOSServiceException(GovPOSServiceErrors.FILE_ERROR);
                }
            }
            jsonFile = XMLtoJsonFile(xmlFile, localDate);
            if(jsonFile == null || !jsonFile.exists()){
                throw  new GovPOSServiceException(GovPOSServiceErrors.FILE_ERROR);
            }
        }

        return GouvPOSServicesUtils.getPDVFromJsonFile(jsonFile);
    }

    private File downloadPDVFile(LocalDate localDate){
        HttpURLConnection httpURLConnection = null;
        InputStream in = null;
        FileOutputStream outputStream = null;
        File targetFile = null;
        try {
            StringBuilder urlString = new StringBuilder(this.ENDPOINT).append("opendata/jour");

            URL url = new URL(urlString.toString());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            httpURLConnection.setReadTimeout(CONNECTION_TIMEOUT);

            //httpURLConnection.getInputStream();
            in = httpURLConnection.getInputStream();

            //File zip
            targetFile = GouvPOSServicesUtils.buildFilePath(localDate, GouvPOSServicesUtils.FileType.ZIP);
            outputStream = new FileOutputStream(targetFile);

            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = in.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            System.out.println("NO ISSUES ON DOWNLOAD");
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){

        } finally {
            if(httpURLConnection != null) try{httpURLConnection.disconnect();} catch (Exception e){}
            if(in != null) try{in.close();} catch (Exception e){}
            if(outputStream != null) try{outputStream.close();} catch (Exception e){}
        }

        return targetFile;
    }

    private void unzipFile(File zippedFile,  LocalDate localDate) throws  GovPOSServiceException{
        if(zippedFile == null || !zippedFile.exists())
             throw new GovPOSServiceException(GovPOSServiceErrors.FILE_ERROR);
        //unzip file extract xml

        ZipInputStream zis = null;
        ZipEntry zipEntry = null;
        File destDir = new File(GouvPOSServicesUtils.LOCAL_FOLDER);
        byte[] buffer = new byte[1024];
        try {
            zis = new ZipInputStream(new FileInputStream(zippedFile));
            zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
        }
        catch(Exception e){

        }
        finally {
            if(zis != null) try{
                zis.closeEntry();
                zis.close();
            } catch (Exception e){};
        }
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private File XMLtoJsonFile(File xmlFile, LocalDate localDate) throws GovPOSServiceException{
        if(localDate == null) throw new GovPOSServiceException(GovPOSServiceErrors.NO_DATE);
        if(xmlFile == null) throw new GovPOSServiceException(GovPOSServiceErrors.FILE_ERROR);



        String data = "";

        try
        {
            // Read the student.xml
            data = readXmlFile(xmlFile);
            ObjectWriter w = new ObjectMapper().writerWithDefaultPrettyPrinter();
            Object o;

            o = new XmlMapper()
                    .registerModule(new SimpleModule().addDeserializer(Object.class, new FixedUntypedObjectDeserializer()))
                    .readValue(data, Object.class);
            return GouvPOSServicesUtils.writeInJsonFile(w.writeValueAsString(o), localDate);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String readXmlFile(File xmlFile) throws GovPOSServiceException{
        if(xmlFile == null || !xmlFile.exists())
            throw new GovPOSServiceException(GovPOSServiceErrors.FILE_ERROR);

        FileInputStream input;
        String result = null;
        try {
            input = new FileInputStream(xmlFile);
            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            decoder.onMalformedInput(CodingErrorAction.IGNORE);
            InputStreamReader reader = new InputStreamReader(input, decoder);
            BufferedReader bufferedReader = new BufferedReader( reader );
            StringBuilder sb = new StringBuilder();
            String line = bufferedReader.readLine();
            while( line != null ) {
                sb.append( line );
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            result = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch( IOException e ) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    public static class FixedUntypedObjectDeserializer extends UntypedObjectDeserializer {

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        protected Object mapObject(JsonParser p, DeserializationContext ctxt) throws IOException {
            String firstKey;

            JsonToken t = p.getCurrentToken();

            if (t == JsonToken.START_OBJECT) {
                firstKey = p.nextFieldName();
            } else if (t == JsonToken.FIELD_NAME) {
                firstKey = p.getCurrentName();
            } else {
                if (t != JsonToken.END_OBJECT) {
                    throw ctxt.mappingException(handledType(), p.getCurrentToken());
                }
                firstKey = null;
            }

            // empty map might work; but caller may want to modify... so better
            // just give small modifiable
            LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>(2);
            if (firstKey == null)
                return resultMap;

            p.nextToken();
            resultMap.put(firstKey, deserialize(p, ctxt));

            // 03-Aug-2016, jpvarandas: handle next objects and create an array
            Set<String> listKeys = new LinkedHashSet<>();

            String nextKey;
            while ((nextKey = p.nextFieldName()) != null) {
                p.nextToken();
                if (resultMap.containsKey(nextKey)) {
                    Object listObject = resultMap.get(nextKey);

                    if (!(listObject instanceof List)) {
                        listObject = new ArrayList<>();
                        ((List) listObject).add(resultMap.get(nextKey));

                        resultMap.put(nextKey, listObject);
                    }

                    ((List) listObject).add(deserialize(p, ctxt));

                    listKeys.add(nextKey);

                }
                else if(nextKey.equals("prix")){
                    Object listObject = resultMap.get(nextKey);
                    if(listObject == null){
                        listObject = new ArrayList<>();
                        resultMap.put(nextKey, listObject);
                    }else{
                        ((List) listObject).add(resultMap.get(nextKey));
                    }
				/*if (!(listObject instanceof List)) {
					listObject = new ArrayList<>();
					((List) listObject).add(resultMap.get(nextKey));

					resultMap.put(nextKey, listObject);
				}*/

                    ((List) listObject).add(deserialize(p, ctxt));

                    listKeys.add(nextKey);
                }else {
                    resultMap.put(nextKey, deserialize(p, ctxt));

                }
            }

            return resultMap;

        }

    }


}
