import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ArxmlSorter{

    public static void main(String[] args){

        /* Start The Programe */
        if(args.length == 0){
            System.out.println("Use : java ArxmlSorter input.arxml"); // Show in CMD first
            return;
        }

        String inputFileName = args[0]; // input file name
        String outputFileName = getOutputFileName(inputFileName); // output file name

        
        try{
            
            /* incorrect File Extension */
            validateFileExtension(inputFileName);

            /* Read xml Document using DOM */
            File inputFile = new File(inputFileName); // inputFile object
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance(); // dbFactory object
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder(); // dBuilder object 
            Document doc = dBuilder.parse(inputFile); // doc object
            doc.getDocumentElement().normalize();

            /* Empty File */
            if (doc.getDocumentElement().getChildNodes().getLength() == 0) { // Get document Element
                throw new EmptyAutosarFileException("Input ARXML file is empty."); // Exeption for Empty File
            }

            /* Node List */
            NodeList containerList = doc.getElementsByTagName("CONTAINER");
            ArrayList<Element> containers = new ArrayList<Element>();
            for (int i = 0; i < containerList.getLength(); i++) {
                containers.add((Element) containerList.item(i));
            }

            /* Collections Sorts */
            Collections.sort(containers, new Comparator<Element>() {
                public int compare(Element e1, Element e2) {
                    String name1 = e1.getElementsByTagName("SHORT-NAME").item(0).getTextContent(); // name 1
                    String name2 = e2.getElementsByTagName("SHORT-NAME").item(0).getTextContent(); // name 2
                    return name1.compareTo(name2); // Compare name1 with name 2
                }
            });

            /* rootElement */
            Element rootElement = doc.getDocumentElement();
            for (Element container : containers) {
                rootElement.appendChild(container); // Adds the node <code>newChild</code> to the end of the list
            }

            /* TransformerFactory */
            TransformerFactory transformerFactory = TransformerFactory.newInstance(); // transformerFactory object
            Transformer transformer = transformerFactory.newTransformer(); // transformer object
            DOMSource source = new DOMSource(doc); // source object "arxml Source"
            StreamResult result = new StreamResult(new File(outputFileName)); // result object "OutPut Target"
            transformer.transform(source, result); // Transform the XML <code>Source</code> to a <code>Result</code>.
            
            /* Print */
            System.out.println("Containers sorted is Done ,  Output written to " + outputFileName); // Print

        }catch (NotVaildAutosarFileException e) { // incorrect File Extension "Exception"
            System.err.println("Error: " + e.getMessage());

        }  catch (SAXException | IOException e) { // Input file does not have any content "Exception"
            System.err.println("Error: Input file does not have any content" );

        }catch (EmptyAutosarFileException e) { // Empty File "Exception"
            System.err.println("Error: " + e.getMessage());

        } catch (Exception e) { // "Exception"
            e.printStackTrace();

        }
    }

            /* Change the Output File Name  */
        private static String getOutputFileName(String inputFileName) { 
            int dotIndex = inputFileName.lastIndexOf("."); // Number of the String
            String fileNameWithoutExtension = inputFileName.substring(0, dotIndex); // Sub String before the Extension
            String fileExtension = inputFileName.substring(dotIndex);  // Sub String the Extension
                return fileNameWithoutExtension + "_mod" + fileExtension; // Add the "_mod" then add the Extension
    }

            /* incorrect File Extension */
        private static void validateFileExtension(String fileName) throws NotVaildAutosarFileException {
            if (!fileName.endsWith(".arxml")) { // Search if  File Extension is correct
                throw new NotVaildAutosarFileException("Input file does not have .arxml extension.");
            }
        }

    }

            /* incorrect File Extension Exception */
        class NotVaildAutosarFileException extends Exception {
            public NotVaildAutosarFileException(String message) {
                super(message);
            }
        }

            /* Empty File Exception */
        class EmptyAutosarFileException extends RuntimeException {
            public EmptyAutosarFileException(String message) {
                super(message);
            }
        }