import PointDeVente.PointDeVente;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

public class XmlReader {

        public void readFile(Long myPositionX, Long myPositionY){
            SAXParserFactory factory = SAXParserFactory.newInstance();

            try {

                SAXParser parser = factory.newSAXParser();
                File file = new File("C:\\Users\\Flo\\Downloads\\PrixCarburants_instantane\\PrixCarburants_instantane.xml");
                //PageHandler pageHandler = new PageHandler(processor);
                PointDeVenteHandlerBase pointDeVenteHandlerBase = new PointDeVenteHandlerBase(0.067294d,48.418621d, 0.20);
                parser.parse(file, pointDeVenteHandlerBase);

                System.out.println();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
}
