import org.junit.Before;
import org.junit.Test;

public class TestReader {

    @Before
    public void setUp(){

    }

    @Test
    public void readFile(){
        XmlReader xmlReader = new XmlReader();
        xmlReader.readFile(0l, 0l);
    }
}
