package asciitomusicxml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import nu.xom.Serializer;
import org.jfugue.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;

/**
 * @author Dilaris Fotis 2017
 */
public class AsciiToMusicXML {
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException {
        System.out.println("Executing java jar file...");
                
        File file = new File("/tmp/my_music.xml");
                
        FileOutputStream fop = new FileOutputStream(file);
        
        if(!file.exists()) file.createNewFile();
        
        MusicXmlRenderer renderer = new MusicXmlRenderer();
        MusicStringParser parser = new MusicStringParser();
        parser.addParserListener(renderer);
        
        
        AsciiParser aParser = new AsciiParser();
        Pattern pattern = new Pattern(aParser.ParseTextFile("/tmp/my_file.txt"));
        
        parser.parse(pattern);        
        
        Serializer serializer = new Serializer(fop, "UTF-8");
        serializer.setIndent(4);
        serializer.write(renderer.getMusicXMLDoc());
   
        // XML edit for musescore
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbFactory.setValidating(false);
        
        DocumentBuilder db = dbFactory.newDocumentBuilder();

	Document doc = db.parse(new FileInputStream(file));
        
        Element element = (Element) doc.getElementsByTagName("key").item(0);
        element.getParentNode().removeChild(element);
        
        
        TransformerFactory transformerFactory =  TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        
        DOMSource source = new DOMSource(doc);
        StreamResult result =
        new StreamResult(new File("/tmp/my_music.xml"));
        transformer.transform(source, result);

              
        //fop.write(content);
        fop.flush();
        fop.close();
       
        System.out.println("Done");     
        
    }
    
}
