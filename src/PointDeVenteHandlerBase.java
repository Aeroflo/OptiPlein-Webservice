
import PointDeVente.PointDeVente;
import Utils.Utils;
import lombok.Builder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Builder
public class PointDeVenteHandlerBase extends DefaultHandler {

    //filters
    double longitude;
    double latitude;
    LocalDateTime localDateTime;
    double perimeterDegree; // 1 = 111km

    //List
    Collection<PointDeVente> listeDePointdeVente = new ArrayList<PointDeVente>();

    //Object
    PointDeVente.PointDeVenteBuilder pdv = null;
    private StringBuilder data = null;

    boolean isAddress;
    boolean isVille;

    public PointDeVenteHandlerBase(double latitude, double longitude, double perimeterDegree) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.perimeterDegree = perimeterDegree;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(qName != null){
            switch (qName){
                case "pdv" :
                    String id = attributes.getValue("id");
                    String latitude = attributes.getValue("latitude");
                    String longitude = attributes.getValue("longitude");
                    pdv = new PointDeVente.PointDeVenteBuilder(id, Utils.parseCoordinate(latitude), Utils.parseCoordinate(longitude));
                    break;
                case "adresse" : if(pdv != null) pdv.address(attributes.getValue(0)); break;
                case "ville": if(pdv!= null) pdv.ville(""); break;
            }
            data = new StringBuilder();
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName != null){
            switch (qName){
                case "pdv" :
                    listeDePointdeVente.add(pdv.build());
                    pdv = null;
                    break;
            }
        }
        super.endElement(uri, localName, qName);
    }
}
