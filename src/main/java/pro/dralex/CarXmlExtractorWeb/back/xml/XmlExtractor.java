package pro.dralex.CarXmlExtractorWeb.back.xml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import pro.dralex.CarXmlExtractorWeb.front.makeView.CarMakeConnectorTmp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class XmlExtractor {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class DrCarMake {
        private int id;
        private String make;
    }

    private List<Car> getDrCarsFromXml(String fileName) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        List<Car> result = new ArrayList<>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        doc.getDocumentElement().normalize();

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList makesList = (NodeList) xPath.compile("/References/Marks/Mark").evaluate(doc, XPathConstants.NODESET);
        log.info("dr total models size:{}", makesList.getLength());
        List<DrCarMake> drCarMakes = new ArrayList<>();
        for (int i = 0; i <= makesList.getLength() - 1; i++) {
            Element elementMark = (Element) makesList.item(i);
            String idMark = elementMark.getElementsByTagName("idMark").item(0).getTextContent();
            String make = elementMark.getElementsByTagName("sMark").item(0).getTextContent();
            drCarMakes.add(new DrCarMake(Integer.parseInt(idMark), make));
        }

        NodeList modelsList = (NodeList) xPath.compile("/References/Models/Model").evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i <= modelsList.getLength() - 1; i++) {
            Element elementMark = (Element) modelsList.item(i);
            int makeId = Integer.parseInt(elementMark.getElementsByTagName("idMark").item(0).getTextContent());
            String model = elementMark.getElementsByTagName("sModel").item(0).getTextContent();
            String make = drCarMakes.stream()
                    .filter(car -> car.id == makeId)
                    .findFirst()
                    .orElse(new DrCarMake(-1, ""))
                    .getMake();
            result.add(new Car(make, model));
        }

        return result;
    }

    private List<Car> getAvCarsFromXml(String fileName) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        List<Car> result = new ArrayList<>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        doc.getDocumentElement().normalize();

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.compile("/Catalog/Make/Model").evaluate(doc, XPathConstants.NODESET);
        log.info("av total model size:{}", nodeList.getLength());
        log.info("");
        for (int i = 0; i <= nodeList.getLength() - 1; i++) {
            Node model = nodeList.item(i);
            NamedNodeMap modelDetail = model.getAttributes();
            Node make = model.getParentNode();
            NamedNodeMap makeDetail = make.getAttributes();
            String modelStr = modelDetail.getNamedItem("name").getNodeValue();
            String makeStr = makeDetail.getNamedItem("name").getNodeValue();
            result.add(new Car(makeStr, modelStr));
        }

        return result;
    }

    private List<Car> getAuCarsFromXml(String fileName) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        List<Car> result = new ArrayList<>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        doc.getDocumentElement().normalize();

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.compile("/catalog/mark/folder").evaluate(doc, XPathConstants.NODESET);
        log.info("au total model size:{}", nodeList.getLength());
        log.info("");
        for (int i = 0; i <= nodeList.getLength() - 1; i++) {
            Node model = nodeList.item(i);
            NamedNodeMap modelDetail = model.getAttributes();
            Node make = model.getParentNode();
            NamedNodeMap makeDetail = make.getAttributes();
            String modelStr = modelDetail.getNamedItem("name").getNodeValue().replaceAll(",.+", "");
            String makeStr = makeDetail.getNamedItem("name").getNodeValue();
            result.add(new Car(makeStr, modelStr));
        }

        return result;
    }

    public List<CarMakeConnectorTmp> getManualRulesForMakes() {
        List<CarMakeConnectorTmp> result = new ArrayList<>();
        result.add(new CarMakeConnectorTmp("ВАЗ (LADA)", "LADA (ВАЗ)", "Лада", ConnectorSource.MANUAL));
        return result;
    }

    private List<CarModelConnector> getManualRulesForModels() {
        List<CarModelConnector> result = new ArrayList<>();

        result.add(new CarModelConnector("Audi", "A4 Allroad Quattro", "A4 allroad", "A4 allroad quattro"));
        result.add(new CarModelConnector("Audi", "A6 Allroad Quattro", "A6 allroad", "A6 allroad quattro"));
        result.add(new CarModelConnector("Audi", "RS2", "RS 2", "RS2"));
        result.add(new CarModelConnector("Audi", "RS3", "RS 3", "RS3"));
        result.add(new CarModelConnector("Audi", "RS4", "RS 4", "RS4"));
        result.add(new CarModelConnector("Audi", "RS5", "RS 5", "RS5"));
        result.add(new CarModelConnector("Audi", "RS6", "RS 6", "RS6"));
        result.add(new CarModelConnector("Audi", "RS7", "RS 7", "RS7"));
        result.add(new CarModelConnector("Audi", "e-tron S Sportback", "e-tron S Sportback", "e-tron Sportback S"));


        result.add(new CarModelConnector("Bmw", "1 серия", "1 серии", "1-Series"));
        result.add(new CarModelConnector("Bmw", "2 серия", "2 серии", "2-Series"));
        result.add(new CarModelConnector("Bmw", "2 серия Active Tourer", "2 серии Active Tourer", "2-Series Active Tourer"));
        result.add(new CarModelConnector("Bmw", "2 серия Gran Tourer", "2 серии Gran Tourer", "2-Series Gran Tourer"));
        result.add(new CarModelConnector("Bmw", "3 серия", "3 серии", "3-Series"));
        result.add(new CarModelConnector("Bmw", "4 серия", "4 серии", "4-Series"));
        result.add(new CarModelConnector("Bmw", "5 серия", "5 серии", "5-Series"));
        result.add(new CarModelConnector("Bmw", "6 серия", "6 серии", "6-Series"));
        result.add(new CarModelConnector("Bmw", "7 серия", "7 серии", "7-Series"));
        result.add(new CarModelConnector("Bmw", "8 серия", "8 серии", "8-Series"));


        result.add(new CarModelConnector("BYD", "Han", "Han DM", "Han", "Han"));
        result.add(new CarModelConnector("BYD", "Han", "Han EV", "Han", "Han"));
        result.add(new CarModelConnector("BYD", "Qin Plus", "Qin", "Qin Plus"));
        result.add(new CarModelConnector("BYD", "Qin Pro", "Qin", "Qin Pro"));
        result.add(new CarModelConnector("BYD", "Tang EV", "Tang", "Tang"));
        result.add(new CarModelConnector("BYD", "Song EV", "Song EV", "Song"));


        result.add(new CarModelConnector("BRILLIANCE", "M1 (BS6)", "M1 (BS6)", "M1"));
        result.add(new CarModelConnector("BRILLIANCE", "M2 (BS4)", "M2 (BS4)", "M2s"));


        result.add(new CarModelConnector("BUGATTI", "Veyron", "EB Veyron 16.4", "Veyron"));


        result.add(new CarModelConnector("BUICK", "Electra", "Electra", "Electra E5"));


        result.add(new CarModelConnector("CADILLAC", "DE Ville", "DeVille", "DeVille"));


        result.add(new CarModelConnector("CHERY", "Amulet (A15)", "Amulet (A15)", "Amulet A15"));
        result.add(new CarModelConnector("CHERY", "Bonus (A13)", "Bonus (A13)", "Bonus A13"));
        result.add(new CarModelConnector("CHERY", "Bonus 3 (E3)", "Bonus 3 (E3/A19)", "Bonus 3 - A19"));
        result.add(new CarModelConnector("CHERY", "CrossEastar (B14)", "CrossEastar (B14)", "CrossEastar B14"));
        result.add(new CarModelConnector("CHERY", "Fora (A21)", "Fora (A21)", "Fora A21"));
        result.add(new CarModelConnector("CHERY", "IndiS (S18D)", "IndiS (S18D)", "indiS S18D"));
        result.add(new CarModelConnector("CHERY", "Kimo (A1)", "Kimo (A1)", "Kimo A1"));
        result.add(new CarModelConnector("CHERY", "M11 (A3)", "M11 (A3)", "M11"));
        result.add(new CarModelConnector("CHERY", "Oriental Son (B11)", "Oriental Son (B11)", "Oriental Son B11"));
        result.add(new CarModelConnector("CHERY", "QQ6 (S21)", "QQ6 (S21)", "QQ6 S21"));
        result.add(new CarModelConnector("CHERY", "Sweet (QQ)", "Sweet (QQ)", "QQ Sweet"));
        result.add(new CarModelConnector("CHERY", "Tiggo (T11)", "Tiggo (T11)", "Tiggo T11"));
        result.add(new CarModelConnector("CHERY", "Very", "Very (A13)", "Very A13"));


        result.add(new CarModelConnector("CHEVROLET", "S10", "S-10 Pickup", "S10"));


        result.add(new CarModelConnector("CHRYSLER", "LeBaron", "LeBaron", "Le Baron"));


        result.add(new CarModelConnector("CITROEN", "2 CV", "2 CV", "2CV"));
        result.add(new CarModelConnector("CITROEN", "DS 3", "DS3", "DS3"));
        result.add(new CarModelConnector("CITROEN", "DS 4", "DS4", "DS4"));
        result.add(new CarModelConnector("CITROEN", "DS 5", "DS5", "DS5"));
        result.add(new CarModelConnector("CITROEN", "Mehari", "E-Mehari", "E-Mehari"));


        result.add(new CarModelConnector("DACIA", "Super Nova", "SuperNova", "SuperNova"));


        result.add(new CarModelConnector("FORD", "Bronco II", "Bronco-II", "Bronco II"));
        result.add(new CarModelConnector("FORD", "Bronco II", "Bronco-II", "Bronco II"));
        result.add(new CarModelConnector("FORD", "F-150", "F-150", "F150"));


        result.add(new CarModelConnector("GEELY", "Geometry A", "Geometry A", "Geometry A GE11"));
        result.add(new CarModelConnector("GEELY", "Geometry C", "Geometry C", "Geometry C GE13"));
        result.add(new CarModelConnector("GEELY", "Coolray", "Coolray", "Coolray SX11"));
        result.add(new CarModelConnector("GEELY", "Tugella", "Tugella", "Tugella FY11"));


        result.add(new CarModelConnector("GREAT WALL", "Cowry", "Cowry (V80)", "Cowrys"));


        result.add(new CarModelConnector("HYUNDAI", "Grand Santa Fe", "Santa Fe Grand", "Grand Santa Fe"));
        result.add(new CarModelConnector("HYUNDAI", "H-1", "H-1", "H1"));
        result.add(new CarModelConnector("HYUNDAI", "H-200", "H200", "H200"));


        result.add(new CarModelConnector("INFINITI", "EX25", "EX25", "EX25"));
        result.add(new CarModelConnector("INFINITI", "EX30", "EX30d", "EX30d"));
        result.add(new CarModelConnector("INFINITI", "EX35", "EX35", "EX35"));
        result.add(new CarModelConnector("INFINITI", "EX37", "EX37", "EX37"));
        result.add(new CarModelConnector("INFINITI", "FX30", "FX30d", "FX30d"));
        result.add(new CarModelConnector("INFINITI", "FX35", "FX35", "FX35"));
        result.add(new CarModelConnector("INFINITI", "FX37", "FX37", "FX37"));
        result.add(new CarModelConnector("INFINITI", "FX45", "FX45", "FX45"));
        result.add(new CarModelConnector("INFINITI", "FX50", "FX50", "FX50"));
        result.add(new CarModelConnector("INFINITI", "G20", "G20", "G20"));
        result.add(new CarModelConnector("INFINITI", "G25", "G25", "G25"));
        result.add(new CarModelConnector("INFINITI", "G35", "G35", "G35"));
        result.add(new CarModelConnector("INFINITI", "G37", "G37", "G37"));
        result.add(new CarModelConnector("INFINITI", "I30", "I30", "I30"));
        result.add(new CarModelConnector("INFINITI", "I35", "I35", "I35"));
        result.add(new CarModelConnector("INFINITI", "JX", "JX", "JX35"));
        result.add(new CarModelConnector("INFINITI", "M25", "M25", "M25"));
        result.add(new CarModelConnector("INFINITI", "M35", "M35", "M35"));
        result.add(new CarModelConnector("INFINITI", "M37", "M37", "M37"));
        result.add(new CarModelConnector("INFINITI", "M45", "M45", "M45"));
        result.add(new CarModelConnector("INFINITI", "M56", "M56", "M56"));
        result.add(new CarModelConnector("INFINITI", "Q40", "Q40", "Q40"));


        result.add(new CarModelConnector("JAC", "J2", "J2 (Yueyue)", "J2"));
        result.add(new CarModelConnector("JAC", "J5", "J5 (Heyue)", "J5"));
        result.add(new CarModelConnector("JAC", "Refine", "M1 (Refine)", "M1"));
        result.add(new CarModelConnector("JAC", "Rein", "S1 (Rein)", "S1"));


        result.add(new CarModelConnector("KIA", "Ceed GT", "Ceed GT", "Ceed"));
        result.add(new CarModelConnector("KIA", "Rio X (X-Line)", "Rio X-Line", "Rio X-Line", "Rio X (X-Line)"));
        result.add(new CarModelConnector("KIA", "Rio X (X-Line)", "Rio X", "Rio X-Line", "Rio X (X-Line)"));


        result.add(new CarModelConnector("LAMBORGHINI", "Huracan", "Huracán", "Huracan"));
        result.add(new CarModelConnector("LAMBORGHINI", "LM002", "LM002", "LM 002"));

        //..CT
        result.add(new CarModelConnector("LEXUS", "CT", "CT 200h", "CT200h"));
        //..ES
        result.add(new CarModelConnector("LEXUS", "ES", "ES 200", "ES200"));
        result.add(new CarModelConnector("LEXUS", "ES", "ES 250", "ES250"));
        result.add(new CarModelConnector("LEXUS", "ES", "ES 300", "ES300"));
        result.add(new CarModelConnector("LEXUS", "ES", "ES 300h", "ES300h"));
        result.add(new CarModelConnector("LEXUS", "ES", "ES 330", "ES330"));
        result.add(new CarModelConnector("LEXUS", "ES", "ES 350", "ES350"));
        //..GS
        result.add(new CarModelConnector("LEXUS", "GS", "GS 200t", "GS200t"));
        result.add(new CarModelConnector("LEXUS", "GS", "GS 250", "GS250"));
        result.add(new CarModelConnector("LEXUS", "GS", "GS 300", "GS300"));
        result.add(new CarModelConnector("LEXUS", "GS", "GS 300h", "GS300h"));
        result.add(new CarModelConnector("LEXUS", "GS", "GS 350", "GS350"));
        result.add(new CarModelConnector("LEXUS", "GS", "GS 400", "GS400"));
        result.add(new CarModelConnector("LEXUS", "GS", "GS 430", "GS430"));
        result.add(new CarModelConnector("LEXUS", "GS", "GS 450h", "GS450h"));
        result.add(new CarModelConnector("LEXUS", "GS", "GS 460", "GS460"));
        result.add(new CarModelConnector("LEXUS", "GX", "GX 460", "GX460"));
        result.add(new CarModelConnector("LEXUS", "GX", "GX 470", "GX470"));
        //..IS
        result.add(new CarModelConnector("LEXUS", "IS", "IS 200", "IS200"));
        result.add(new CarModelConnector("LEXUS", "IS", "IS 200t", "IS200t"));
        result.add(new CarModelConnector("LEXUS", "IS", "IS 220d", "IS220d"));
        result.add(new CarModelConnector("LEXUS", "IS", "IS 250", "IS250"));
        result.add(new CarModelConnector("LEXUS", "IS", "IS 250", "IS250C"));
        result.add(new CarModelConnector("LEXUS", "IS", "IS 300", "IS300"));
        result.add(new CarModelConnector("LEXUS", "IS", "IS 300", "IS300C"));
        result.add(new CarModelConnector("LEXUS", "IS", "IS 300h", "IS300h"));
        result.add(new CarModelConnector("LEXUS", "IS", "IS 350", "IS350"));
        result.add(new CarModelConnector("LEXUS", "IS", "IS 350", "IS350C"));
        result.add(new CarModelConnector("LEXUS", "IS", "IS 500", "IS500"));
        //..LC
        result.add(new CarModelConnector("LEXUS", "LC", "LC 500", "LC500"));
        result.add(new CarModelConnector("LEXUS", "LC", "LC 500h", "LC500h"));
        //..LS
        result.add(new CarModelConnector("LEXUS", "LS", "LS 350", "LS350"));
        result.add(new CarModelConnector("LEXUS", "LS", "LS 400", "LS400"));
        result.add(new CarModelConnector("LEXUS", "LS", "LS 430", "LS430"));
        result.add(new CarModelConnector("LEXUS", "LS", "LS 460", "LS460"));
        result.add(new CarModelConnector("LEXUS", "LS", "LS Long 460 L", "LS460L"));
        result.add(new CarModelConnector("LEXUS", "LS", "LS 500", "LS500"));
        result.add(new CarModelConnector("LEXUS", "LS", "LS 500h", "LS500h"));
        result.add(new CarModelConnector("LEXUS", "LS", "LS 600h", "LS600h"));
        result.add(new CarModelConnector("LEXUS", "LS", "LS Long 600 L", "LS600hL"));
        //..LX
        result.add(new CarModelConnector("LEXUS", "LX", "LX 450", "LX450"));
        result.add(new CarModelConnector("LEXUS", "LX", "LX 450d", "LX450d"));
        result.add(new CarModelConnector("LEXUS", "LX", "LX 470", "LX470"));
        result.add(new CarModelConnector("LEXUS", "LX", "LX 570", "LX570"));
        result.add(new CarModelConnector("LEXUS", "LX", "LX Arctic Trucks 450D AT33", "LX450d"));
        result.add(new CarModelConnector("LEXUS", "LX", "LX Arctic Trucks 450D AT35", "LX450d"));
        result.add(new CarModelConnector("LEXUS", "LX", "LX Arctic Trucks 570 AT33", "LX570"));
        result.add(new CarModelConnector("LEXUS", "LX", "LX Arctic Trucks 450D AT35", "LX570"));
        result.add(new CarModelConnector("LEXUS", "LX", "LX 600", "LX600"));
        //..NX
        result.add(new CarModelConnector("LEXUS", "NX", "NX 200", "NX200"));
        result.add(new CarModelConnector("LEXUS", "NX", "NX 200t", "NX200t"));
        result.add(new CarModelConnector("LEXUS", "NX", "NX 250", "NX250"));
        result.add(new CarModelConnector("LEXUS", "NX", "NX 300", "NX300"));
        result.add(new CarModelConnector("LEXUS", "NX", "NX 300h", "NX300h"));
        result.add(new CarModelConnector("LEXUS", "NX", "NX 350", "NX350"));
        result.add(new CarModelConnector("LEXUS", "NX", "NX 350h", "NX350h"));
        result.add(new CarModelConnector("LEXUS", "NX", "NX 450h+", "NX450h+"));
        //..RC
        result.add(new CarModelConnector("LEXUS", "RC", "RC 200t", "RC200t"));
        result.add(new CarModelConnector("LEXUS", "RC", "RC 300", "RC300"));
        result.add(new CarModelConnector("LEXUS", "RC", "RC 300h", "RC300h"));
        result.add(new CarModelConnector("LEXUS", "RC", "RC 350", "RC350"));
        //..RX
        result.add(new CarModelConnector("LEXUS", "RX", "RX 200t", "RX200t"));
        result.add(new CarModelConnector("LEXUS", "RX", "RX 270", "RX270"));
        result.add(new CarModelConnector("LEXUS", "RX", "RX 300", "RX300"));
        result.add(new CarModelConnector("LEXUS", "RX", "RX 330", "RX330"));
        result.add(new CarModelConnector("LEXUS", "RX", "RX 350", "RX350"));
        result.add(new CarModelConnector("LEXUS", "RX", "RX 350L", "RX350L"));
        result.add(new CarModelConnector("LEXUS", "RX", "RX 350h", "RX350h"));
        result.add(new CarModelConnector("LEXUS", "RX", "RX 400h", "RX400h"));
        result.add(new CarModelConnector("LEXUS", "RX", "RX 450h", "RX450h"));
        result.add(new CarModelConnector("LEXUS", "RX", "RX 450h+", "RX450h+"));
        result.add(new CarModelConnector("LEXUS", "RX", "RX 450hL", "RX450hL"));
        result.add(new CarModelConnector("LEXUS", "RX", "RX 500h", "RX500h"));
        //..SC
        result.add(new CarModelConnector("LEXUS", "SC", "SC 300", "SC300"));
        result.add(new CarModelConnector("LEXUS", "SC", "SC 400", "SC400"));
        result.add(new CarModelConnector("LEXUS", "SC", "SC 430", "SC430"));

        //..UX
        result.add(new CarModelConnector("LEXUS", "UX", "UX 200", "UX200"));
        result.add(new CarModelConnector("LEXUS", "UX", "UX 250h", "UX250h"));
        result.add(new CarModelConnector("LEXUS", "UX", "UX 300e", "UX300e"));

        // todo: mini brands are very special, not all here
        //..Clubman
        result.add(new CarModelConnector("MINI", "Clubman", "Cooper Clubman", "Clubman Cooper", "Clubman"));
        result.add(new CarModelConnector("MINI", "Clubman", "Cooper Clubman", "Clubman Cooper D", "Clubman"));
        result.add(new CarModelConnector("MINI", "Clubman", "Cooper S Clubman", "Clubman Cooper S", "Clubman"));
        result.add(new CarModelConnector("MINI", "Clubman", "Cooper S Clubman", "Clubman Cooper S ALL4", "Clubman"));
        result.add(new CarModelConnector("MINI", "Clubman", "Cooper SD Clubman", "Clubman Cooper SD", "Clubman"));
        result.add(new CarModelConnector("MINI", "Clubman", "Cooper SD Clubman", "Clubman Cooper SD ALL4", "Clubman"));
        result.add(new CarModelConnector("MINI", "Clubman", "John Cooper Works Clubman", "Clubman JCW John Cooper Works", "Clubman"));
        result.add(new CarModelConnector("MINI", "Clubman", "One", "Clubman One", "Clubman"));
        result.add(new CarModelConnector("MINI", "Clubman", "One", "Clubman One D", "Clubman"));
        //..Countryman
        result.add(new CarModelConnector("MINI", "Countryman", "Cooper Countryman", "Countryman Cooper", "Countryman"));
        result.add(new CarModelConnector("MINI", "Countryman", "Cooper Countryman", "Countryman Cooper D", "Countryman"));
        result.add(new CarModelConnector("MINI", "Countryman", "Cooper S Countryman", "Countryman Cooper S", "Countryman"));
        result.add(new CarModelConnector("MINI", "Countryman", "Cooper SE Countryman", "Countryman Cooper S E", "Countryman"));
        result.add(new CarModelConnector("MINI", "Countryman", "Cooper SD Countryman", "Countryman Cooper SD", "Countryman"));
        result.add(new CarModelConnector("MINI", "Countryman", "John Cooper Works Countryman", "Countryman JCW John Cooper Works", "Countryman"));
        //..Paceman
        result.add(new CarModelConnector("MINI", "Paceman", "Cooper Paceman", "Paceman Cooper", "Paceman"));
        result.add(new CarModelConnector("MINI", "Paceman", "Cooper Paceman", "Paceman Cooper D", "Paceman"));
        result.add(new CarModelConnector("MINI", "Paceman", "Cooper S Paceman", "Paceman Cooper S", "Paceman"));
        result.add(new CarModelConnector("MINI", "Paceman", "John Cooper Works Paceman", "Paceman JCW John Cooper Works", "Paceman"));

        result.add(new CarModelConnector("MAZDA", "2", "2", "Mazda2"));
        result.add(new CarModelConnector("MAZDA", "3", "3", "Mazda3"));
        result.add(new CarModelConnector("MAZDA", "3 MPS", "3 MPS", "Mazda3 MPS"));
        result.add(new CarModelConnector("MAZDA", "5", "5", "Mazda5"));
        result.add(new CarModelConnector("MAZDA", "6", "6", "Mazda6"));
        result.add(new CarModelConnector("MAZDA", "6 MPS", "6 MPS", "Mazda6 MPS"));
        result.add(new CarModelConnector("MAZDA", "Bongo", "Bongo", "Bongo Brawny"));
        result.add(new CarModelConnector("MAZDA", "Bongo", "Bongo", "Bongo Brawny Truck"));
        result.add(new CarModelConnector("MAZDA", "Bongo", "Bongo", "Bongo Truck"));

        //.. todo: MERCEDES-BENZ in auto.ru has: "a-class 320 I rest"
        result.add(new CarModelConnector("MERCEDES-BENZ", "190", "190 (W201)", "190 (W201)", "190"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "190", "190 SL", "190 SL", "190"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "220 (W187)", "220 (W187)", "220 (W187)", "W187"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "A-класс", "A-класс", "A-Класс", "A-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "A-класс", "A-класс AMG", "A-Класс AMG", "A-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "B-класс", "B-Класс", "B-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "C-класс", "C-класс", "C-Класс", "C-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "C-класс", "C-класс AMG", "C-Класс AMG", "C-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "CL-класс", "CL-класс", "CL-Класс", "CL-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "CL-класс", "CL-класс AMG", "CL-Класс AMG", "CL-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "CLA-класс", "CLA-класс", "CLA", "CLA-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "CLA-класс", "CLA-класс AMG", "CLA AMG", "CLA-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "CLC-класс", "CLC-Класс", "CLC-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "CLK-класс", "CLK-Класс", "CLK-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "CLK-класс","CLK-класс AMG", "CLK-Класс AMG", "CLK-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "CLS-класс", "CLS-класс", "CLS", "CLS-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "CLS-класс", "CLS-класс AMG", "CLS AMG", "CLS-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "E-класс", "E-класс", "E-Класс", "E-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "E-класс", "E-класс AMG", "E-Класс AMG", "E-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "E-класс", "E-класс All-Terrain", "", ""));

        result.add(new CarModelConnector("MERCEDES-BENZ", "EQE", "EQE AMG", "EQE AMG", "EQE"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "G-класс", "G-класс", "G-Класс", "G-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "G-класс", "G-класс AMG", "G-Класс AMG", "G-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "GL-класс", "GL-класс", "GL-Класс", "GL-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "GL-класс", "GL-класс AMG", "GL-Класс AMG", "GL-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "GLA-класс", "GLA-класс", "GLA", "GLA-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "GLA-класс", "GLA-класс AMG", "GLA AMG", "GLA-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "GLB-класс", "GLB-класс", "GLB", "GLB-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "GLB-класс", "GLB-класс AMG", "GLB AMG", "GLB-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "GLC-класс", "GLC-класс", "GLC", "GLC"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "GLC-класс", "GLC-класс AMG", "GLC AMG", "GLC"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "GLC-класс Coupe", "GLC-класс Coupe", "GLC Coupe", "GLC Coupe"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "GLC-класс Coupe", "GLC-класс AMG Coupe", "GLC Coupe AMG", "GLC Coupe"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "GLE-класс", "GLE-класс", "GLE", "GLE"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "GLE-класс", "GLE-класс AMG", "GLE AMG", "GLE"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "GLE-класс Coupe", "GLE-класс Coupe", "GLE Coupe", "GLE Coupe"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "GLE-класс Coupe", "GLE-класс AMG Coupe", "GLE Coupe AMG", "GLE Coupe"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "GLK-класс", "GLK-класс", "GLK-Класс", "GLK-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "GLS-класс", "GLS-класс", "GLS", "GLS-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "GLS-класс", "GLS-класс AMG", "GLS AMG", "GLS-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "M-класс", "M-класс", "M-Класс", "M-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "M-класс", "M-класс AMG", "M-Класс AMG", "M-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "R-класс", "R-класс", "R-Класс", "R-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "R-класс", "R-класс AMG", "R-Класс AMG", "R-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "S-класс", "S-класс", "S-Класс", "S-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "S-класс", "S-класс AMG", "S-Класс AMG", "S-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "SL-класс", "SL-класс", "SL-Класс", "SL-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "SL-класс", "SL-класс AMG", "SL-Класс AMG", "SL-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "SLC-класс", "SLC-класс", "SLC", "SLC-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "SLC-класс", "SLC-класс AMG", "SLC AMG", "SLC-Class"));

        result.add(new CarModelConnector("MERCEDES-BENZ", "SLK-класс", "SLK-класс", "SLK-Класс", "SLK-Class"));
        result.add(new CarModelConnector("MERCEDES-BENZ", "SLK-класс", "SLK-класс AMG", "SLK-Класс AMG", "SLK-Class"));


        result.add(new CarModelConnector("MITSUBISHI", "Delica D2", "Delica D:2", "Delica D:2"));
        result.add(new CarModelConnector("MITSUBISHI", "Delica D3", "Delica D:3", "Delica D:3"));
        result.add(new CarModelConnector("MITSUBISHI", "Delica D5", "Delica D:5", "Delica D:5"));


        result.add(new CarModelConnector("NISSAN", "Navara", "Navara (Frontier)", "Navara"));

        //..911
        result.add(new CarModelConnector("PORSCHE", "911", "911 Carrera", "911 Carrera", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 Carrera 4", "911 Carrera 4", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 Carrera 4 GTS", "911 Carrera 4 GTS", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 Carrera 4S", "911 Carrera 4S", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 Carrera GTS", "911 Carrera GTS", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 Carrera S", "911 Carrera S", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 GT2", "911 GT2", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 GT2 RS", "911 GT2 RS", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 GT3", "911 GT3", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 GT3 RS", "911 GT3 RS", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 Speedster", "911 Speedster", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 Targa 4", "911 Targa 4", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 Targa 4 GTS", "911 Targa 4 GTS", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 Targa 4S", "911 Targa 4S", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 Turbo", "911 Turbo", "911"));
        result.add(new CarModelConnector("PORSCHE", "911", "911 Turbo S", "911 Turbo S", "911"));
        //..boxster
        result.add(new CarModelConnector("PORSCHE", "Boxster", "718 Boxster", "Boxster", "Boxster"));
        result.add(new CarModelConnector("PORSCHE", "Boxster", "718 Boxster S", "Boxster S", "Boxster"));
        result.add(new CarModelConnector("PORSCHE", "Boxster", "Boxster", "", "Boxster"));
        result.add(new CarModelConnector("PORSCHE", "Boxster", "Boxster S", "", "Boxster"));
        result.add(new CarModelConnector("PORSCHE", "Boxster", "", "Boxster", "Boxster"));
        result.add(new CarModelConnector("PORSCHE", "Boxster", "", "Boxster 718", "Boxster"));
        result.add(new CarModelConnector("PORSCHE", "Boxster", "", "Boxster 718 GTS", "Boxster"));
        result.add(new CarModelConnector("PORSCHE", "Boxster", "", "Boxster 718 GTS 4.0", "Boxster"));
        result.add(new CarModelConnector("PORSCHE", "Boxster", "", "Boxster 718 S", "Boxster"));
        result.add(new CarModelConnector("PORSCHE", "Boxster", "", "Boxster GTS", "Boxster"));
        result.add(new CarModelConnector("PORSCHE", "Boxster", "", "Boxster S", "Boxster"));
        result.add(new CarModelConnector("PORSCHE", "Boxster", "", "Boxster Spyder", "Boxster"));
        //..cayman
        result.add(new CarModelConnector("PORSCHE", "Cayman", "718 Cayman", "Cayman 718", "Cayman"));
        result.add(new CarModelConnector("PORSCHE", "Cayman", "718 Cayman S", "Cayman 718 S", "Cayman"));
        result.add(new CarModelConnector("PORSCHE", "Cayman", "Cayman", "Cayman", "Cayman"));
        result.add(new CarModelConnector("PORSCHE", "Cayman", "Cayman GT4", "Cayman 718", "Cayman"));
        result.add(new CarModelConnector("PORSCHE", "Cayman", "Cayman GTS", "Cayman 718 GTS", "Cayman"));
        result.add(new CarModelConnector("PORSCHE", "Cayman", "Cayman S", "Cayman 718 GTS 4.0", "Cayman"));
        result.add(new CarModelConnector("PORSCHE", "Cayman", "", "Cayman 718 S", "Cayman"));
        result.add(new CarModelConnector("PORSCHE", "Cayman", "", "Cayman 718 T", "Cayman"));
        result.add(new CarModelConnector("PORSCHE", "Cayman", "", "Cayman GTS", "Cayman"));
        result.add(new CarModelConnector("PORSCHE", "Cayman", "", "Cayman R", "Cayman"));
        result.add(new CarModelConnector("PORSCHE", "Cayman", "", "Cayman S", "Cayman"));
        //.. macan
        result.add(new CarModelConnector("PORSCHE", "Macan", "Macan", "Macan", "Macan"));
        result.add(new CarModelConnector("PORSCHE", "Macan", "Macan", "Macan Diesel", "Macan"));
        result.add(new CarModelConnector("PORSCHE", "Macan", "Macan GTS", "Macan GTS", "Macan"));
        result.add(new CarModelConnector("PORSCHE", "Macan", "Macan S", "Macan S", "Macan"));
        result.add(new CarModelConnector("PORSCHE", "Macan", "Macan Turbo", "Macan Turbo", "Macan"));
        result.add(new CarModelConnector("PORSCHE", "Macan", "", "Macan S Diesel", "Macan"));
        result.add(new CarModelConnector("PORSCHE", "Macan", "", "Macan T", "Macan"));
        //..cayenne coupe
        result.add(new CarModelConnector("PORSCHE", "Cayenne Coupe", "Cayenne Coupe", "Cayenne Coupé", "Cayenne Coupe"));
        result.add(new CarModelConnector("PORSCHE", "Cayenne Coupe", "Cayenne Coupe", "Cayenne S Coupé", "Cayenne Coupe"));
        result.add(new CarModelConnector("PORSCHE", "Cayenne Coupe", "Cayenne Coupe", "Cayenne GTS Coupé", "Cayenne Coupe"));
        result.add(new CarModelConnector("PORSCHE", "Cayenne Coupe", "Cayenne Coupe", "Cayenne Turbo Coupé", "Cayenne Coupe"));
        result.add(new CarModelConnector("PORSCHE", "Cayenne Coupe", "Cayenne Coupe", "Cayenne Turbo S E-Hybrid Coupé", "Cayenne Coupe"));
        //..cayenne
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "Cayenne S", "", "Cayenne"));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "Cayenne Turbo", "", "Cayenne"));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "Cayenne Turbo S", "", "Cayenne"));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "", "Cayenne Diesel", ""));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "", "Cayenne E-Hybrid Coupé", ""));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "", "Cayenne GTS", ""));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "", "Cayenne S", ""));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "", "Cayenne S Diesel", ""));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "", "Cayenne S E-Hybrid", ""));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "", "Cayenne S Hybrid", ""));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "", "Cayenne Turbo", ""));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "", "Cayenne Turbo GT", ""));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "", "Cayenne Turbo S", ""));
        result.add(new CarModelConnector("PORSCHE", "Cayenne", "", "Cayenne Turbo S E-Hybrid", ""));
        //..panamera
        result.add(new CarModelConnector("PORSCHE", "Panamera", "Panamera 4", "", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "Panamera 4S", "", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "Panamera GTS", "", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "Panamera S", "", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "Panamera Turbo", "", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "Panamera Turbo S", "", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4 E-Hybrid", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4 E-Hybrid Executive", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4 E-Hybrid Sport Turismo", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4 Executive", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4 Sport Turismo", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4S", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4S Diesel", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4S Diesel Sport Turismo", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4S E-Hybrid", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4S E-Hybrid Executive", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4S E-Hybrid Sport Turismo", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4S Executive", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera 4S Sport Turismo", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera Diesel", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera GTS", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera GTS Sport Turismo", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera S", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera S E-Hybrid", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera S Hybrid", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera Turbo", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera Turbo Executive", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera Turbo S", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera Turbo S E-Hybrid", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera Turbo S E-Hybrid Executive", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera Turbo S E-Hybrid Sport Turismo", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera Turbo S Executive", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera Turbo S Sport Turismo", "Panamera"));
        result.add(new CarModelConnector("PORSCHE", "Panamera", "", "Panamera Turbo Sport Turismo", "Panamera"));
        //..taycan
        result.add(new CarModelConnector("PORSCHE", "Taycan", "Taycan Cross Turismo", "Taycan 4 Cross Turismo", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "", "Taycan 4S", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "", "Taycan 4S Cross Turismo", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "", "Taycan 4S Sport Turismo", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "", "Taycan GTS", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "", "Taycan GTS Sport Turismo", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "Taycan Sport Turismo", "Taycan Sport Turismo", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "", "Taycan Turbo", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "", "Taycan Turbo Cross Turismo", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "", "Taycan Turbo S", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "", "Taycan Turbo S Cross Turismo", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "", "Taycan Turbo S Sport Turismo", "Taycan"));
        result.add(new CarModelConnector("PORSCHE", "Taycan", "", "Taycan Turbo Sport Turismo", "Taycan"));


        result.add(new CarModelConnector("RAVON", "Nexia", "Nexia R3", "Nexia R3"));


        result.add(new CarModelConnector("RENAULT", "LOGAN", "Logan Stepway", "Logan Stepway", "Logan Stepway"));
        result.add(new CarModelConnector("RENAULT", "Sandero", "Sandero Stepway", "Sandero Stepway", "Sandero Stepway"));
        result.add(new CarModelConnector("RENAULT", "Scenic", "Grand Scenic", "Grand Scenic", "Scenic"));


        result.add(new CarModelConnector("SSANGYONG", "Tivoli XLV", "XLV", "Tivoli XLV"));


        result.add(new CarModelConnector("ГАЗ", "13 Чайка", "13 «Чайка»", "13 Чайка"));
        result.add(new CarModelConnector("ГАЗ", "14 Чайка", "14 «Чайка»", "14 Чайка"));
        result.add(new CarModelConnector("ГАЗ", "21 Волга", "21 «Волга»", "21 Волга"));
        result.add(new CarModelConnector("ГАЗ", "22 Волга", "22 «Волга»", "22 Волга"));
        result.add(new CarModelConnector("ГАЗ", "2330 Тигр", "2330 «Тигр»", "2330 Тигр"));
        result.add(new CarModelConnector("ГАЗ", "24 Волга", "24 Волга", "24 Волга"));
        result.add(new CarModelConnector("ГАЗ", "3102 Волга", "3102 «Волга»", "3102 Волга"));
        result.add(new CarModelConnector("ГАЗ", "31029 Волга", "31029 «Волга»", "31029 Волга"));
        result.add(new CarModelConnector("ГАЗ", "3105 Волга", "3105 «Волга»", "3105 Волга"));
        result.add(new CarModelConnector("ГАЗ", "3110 Волга", "3110 «Волга»", "3110 Волга"));
        result.add(new CarModelConnector("ГАЗ", "31105 Волга", "31105 «Волга»", "31105 Волга"));
        result.add(new CarModelConnector("ГАЗ", "3111 Волга", "3111 «Волга»", "3111 Волга"));
        result.add(new CarModelConnector("ГАЗ", "Volga Siber", "Volga Siber", "Волга Сайбер"));
        result.add(new CarModelConnector("ГАЗ", "M1", "М1", "М1"));
        result.add(new CarModelConnector("ГАЗ", "М-20 Победа", "М-20 «Победа»", "Победа"));

        result.add(new CarModelConnector("ЗАЗ", "Lanos", "Lanos", "Ланос"));
        result.add(new CarModelConnector("ЗАЗ", "Chance", "Chance", "Шанс"));
        result.add(new CarModelConnector("ЗАЗ", "Sens", "Sens", "Сенс"));
        result.add(new CarModelConnector("ЗАЗ", "965 Запорожец", "965", "Запорожец"));
        result.add(new CarModelConnector("ЗАЗ", "966 Запорожец", "966", "Запорожец"));
        result.add(new CarModelConnector("ЗАЗ", "968 Запорожец", "968", "Запорожец"));


        result.add(new CarModelConnector("УАЗ", "Буханка", "2206", "", "Буханка"));
        result.add(new CarModelConnector("УАЗ", "Буханка", "29891", "", "Буханка"));
        result.add(new CarModelConnector("УАЗ", "Буханка", "3741", "", "Буханка"));
        result.add(new CarModelConnector("УАЗ", "Буханка", "3909", "", "Буханка"));
        result.add(new CarModelConnector("УАЗ", "Буханка", "3962", "", "Буханка"));
        result.add(new CarModelConnector("УАЗ", "Буханка", "450", "", "Буханка"));
        result.add(new CarModelConnector("УАЗ", "Буханка", "452 Буханка", "", "Буханка"));

        result.add(new CarModelConnector("УАЗ", "Пикап", "Pickup", "Pickup", "Пикап"));
        result.add(new CarModelConnector("УАЗ", "Пикап", "2360", "", "Пикап"));
        result.add(new CarModelConnector("УАЗ", "Пикап", "3303", "", "Пикап"));
        result.add(new CarModelConnector("УАЗ", "Пикап", "39094", "", "Пикап"));
        result.add(new CarModelConnector("УАЗ", "Пикап", "39095", "", "Пикап"));
        result.add(new CarModelConnector("УАЗ", "Пикап", "Карго", "Pickup", "Карго"));
        result.add(new CarModelConnector("УАЗ", "Пикап", "", "", "Профи"));

        result.add(new CarModelConnector("УАЗ","Patriot", "Patriot", "Патриот"));
        result.add(new CarModelConnector("УАЗ","Patriot", "Patriot Sport", "Патриот Спорт"));
        result.add(new CarModelConnector("УАЗ","Patriot", "Patriot Sport", "Патриот Спорт"));

        result.add(new CarModelConnector("УАЗ", "Симбир", "Симбир", "3162 Simbir", "Симбир"));
        result.add(new CarModelConnector("УАЗ", "Hunter", "Hunter", "Hunter", "Хантер"));

        result.add(new CarModelConnector("LIFAN","Breez (520)", "Breez (520)", "Breez"));
        result.add(new CarModelConnector("LIFAN","Cebrium (720)", "Cebrium (720)", "Cebrium"));
        result.add(new CarModelConnector("LIFAN","Celliya (530)", "Celliya (530)", "Celliya"));
        result.add(new CarModelConnector("LIFAN","Murman", "Murman (820)", "Murman"));
        result.add(new CarModelConnector("LIFAN","Smily (320)", "Smily", "Smily"));

        result.add(new CarModelConnector("ВАЗ (LADA)","2113 Samara", "2113", "2113 Самара"));
        result.add(new CarModelConnector("ВАЗ (LADA)","2114 Samara", "2114", "2114 Самара"));
        result.add(new CarModelConnector("ВАЗ (LADA)","2115 Samara", "2115", "2115 Самара"));

        result.add(new CarModelConnector("ВАЗ (LADA)","2121 (4x4) Нива", "2121 (4x4) Bronto", "2121 (4x4) Bronto", "4x4 2121 Нива"));
        result.add(new CarModelConnector("ВАЗ (LADA)","2121 (4x4) Нива", "2121 (4x4) Urban", "2121 (4x4) Urban", "4x4 Бронто"));
        result.add(new CarModelConnector("ВАЗ (LADA)","2121 (4x4) Нива", "2121 (4x4) Рысь", "2121 (4x4) Рысь", "4x4 Урбан"));
        result.add(new CarModelConnector("ВАЗ (LADA)","2121 (4x4) Нива", "2121 (4x4) Фора", "2121 (4x4) Фора", ""));
        result.add(new CarModelConnector("ВАЗ (LADA)","2121 (4x4) Нива", "", "2121 (4x4)", ""));

        result.add(new CarModelConnector("ВАЗ (LADA)","2131 (4x4) Нива", "2131 (4x4) Urban", "2131 (4x4) Urban", "4x4 2131 Нива"));
        result.add(new CarModelConnector("ВАЗ (LADA)","2131 (4x4) Нива", "2131 (4x4) Рысь", "2131 (4x4) Рысь", "4x4 2131 Нива"));
        result.add(new CarModelConnector("ВАЗ (LADA)","2131 (4x4) Нива", "4x4 (Нива)", "2131 (4x4)", "4x4 2131 Нива"));

        result.add(new CarModelConnector("ВАЗ (LADA)","Niva", "Niva", "Нива (2020-21 гг.)"));
        result.add(new CarModelConnector("ВАЗ (LADA)","Niva Off-road", "Niva Off-road", ""));
        result.add(new CarModelConnector("ВАЗ (LADA)","Niva Travel", "Niva Travel", "Нива Тревел"));
        result.add(new CarModelConnector("ВАЗ (LADA)","Niva Legend", "Niva Legend", "Нива Легенд"));
        result.add(new CarModelConnector("ВАЗ (LADA)","Niva Legend Bronto", "Niva Legend Bronto", "Нива Бронто"));

        result.add(new CarModelConnector("ВАЗ (LADA)","Granta", "Granta", "Гранта"));
        result.add(new CarModelConnector("ВАЗ (LADA)","Granta", "Granta Sport", "Гранта Спорт"));
        result.add(new CarModelConnector("ВАЗ (LADA)","Granta Cross", "Granta Cross", "Гранта Кросс"));

        result.add(new CarModelConnector("ВАЗ (LADA)","Kalina", "Kalina", "Калина"));
        result.add(new CarModelConnector("ВАЗ (LADA)","Kalina Cross", "Kalina Cross", "Калина Кросс"));
        result.add(new CarModelConnector("ВАЗ (LADA)","Kalina", "Kalina Sport", "Калина Спорт"));

        result.add(new CarModelConnector("ВАЗ (LADA)","Largus", "Largus", "Ларгус"));
        result.add(new CarModelConnector("ВАЗ (LADA)","Largus Cross", "Largus Cross", "Ларгус Кросс"));

        result.add(new CarModelConnector("ВАЗ (LADA)","Priora", "Priora", "Приора"));

        result.add(new CarModelConnector("ВАЗ (LADA)","Vesta", "Vesta", "Веста"));
        result.add(new CarModelConnector("ВАЗ (LADA)","Vesta", "Vesta Sport", "Веста Спорт"));
        result.add(new CarModelConnector("ВАЗ (LADA)","Vesta", "Vesta SW", ""));
        result.add(new CarModelConnector("ВАЗ (LADA)","Vesta", "Vesta SW Cross", ""));
        result.add(new CarModelConnector("ВАЗ (LADA)","Vesta Cross", "Vesta Cross", "Веста Кросс"));

        result.add(new CarModelConnector("ВАЗ (LADA)","XRAY", "XRAY", "Х-рей"));
        result.add(new CarModelConnector("ВАЗ (LADA)","XRAY Cross", "XRAY Cross", "Х-рей Кросс"));

        return result;
    }



    public MakesResultFromXml getSupportedMakes(String avFileName, String auFilename, String drFileName) throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        List<Car> avitoCars = getAvCarsFromXml(avFileName);
        List<Car> autoruCars = getAuCarsFromXml(auFilename);
        List<Car> dromCars = getDrCarsFromXml(drFileName);
//        List<CarMakeConnector> manualMakes = getManualRulesForMakes();
//        return showSupportedMakes(avitoCars, autoruCars, dromCars, manualMakes);
        return showSupportedMakes(avitoCars, autoruCars, dromCars);
    }

    private MakesResultFromXml showSupportedMakes(List<Car> avitoCars,
                                                  List<Car> autoruCars,
                                                  List<Car> dromCars
//                                                      List<CarMakeConnector> rulesManual
    ) {

        List<String> avitoMakeList = new ArrayList<>(avitoCars.stream()
                .map(Car::getMake)
                .distinct()
                .sorted()
                .toList());

        List<String> autoruMakeList = new ArrayList<>(autoruCars.stream()
                .map(Car::getMake)
                .distinct()
                .sorted()
                .toList());

        List<String> dromMakeList = new ArrayList<>(dromCars.stream()
                .map(Car::getMake)
                .distinct()
                .sorted()
                .toList());


        MakesResultFromXml supportedMakes = new MakesResultFromXml();
        List<String> makes = findIntersection(avitoMakeList, autoruMakeList, dromMakeList);
        makes.stream().sorted().forEach(make -> {
            avitoMakeList.remove(make);
            autoruMakeList.remove(make);
            dromMakeList.remove(make);
            supportedMakes.getSupportedMakes().add(new CarMakeConnectorTmp(make, make, make, ConnectorSource.AUTO_XML));
        });


        List<CarMakeConnectorTmp> rulesAutomatic = new ArrayList<>();
        List<String> avitoModelsTmp = new ArrayList<>(avitoMakeList);
        avitoModelsTmp.forEach(avitoModelTmp -> {
                    Optional<String> autoruModel = autoruMakeList.stream().filter(item -> item.equalsIgnoreCase(avitoModelTmp)).findFirst();
                    Optional<String> dromModel = dromMakeList.stream().filter(item -> item.equalsIgnoreCase(avitoModelTmp)).findFirst();
                    if (autoruModel.isPresent() && dromModel.isPresent()) {
                        rulesAutomatic.add(new CarMakeConnectorTmp(avitoModelTmp, autoruModel.get(), dromModel.get(), ConnectorSource.AUTO_XML));
                        avitoMakeList.remove(avitoModelTmp);
                        autoruMakeList.remove(autoruModel.get());
                        dromMakeList.remove(dromModel.get());
                    }
                }
        );

        rulesAutomatic.forEach(model -> {
            supportedMakes.getSupportedMakes().add(
                    new CarMakeConnectorTmp(
                            model.getAvStyle(),
                            model.getAuStyle(),
                            model.getDrStyle(),
                            ConnectorSource.AUTO_XML)
            );
        });

//        rulesManual.forEach(rule -> {
//            avitoMakeList.remove(rule.getAvStyle());
//            autoruMakeList.remove(rule.getAuStyle());
//            dromMakeList.remove(rule.getDrStyle());
//            supportedMakes.getSupportedMakes().add(new CarMakeConnector(rule.getAvStyle(), rule.getAuStyle(), rule.getDrStyle()));
//        });

        int maxIndex = Collections.max(List.of(avitoMakeList.size(), autoruMakeList.size(), dromMakeList.size()));
        for (int i = 0; i < maxIndex; i++) {
            if (i <= avitoMakeList.size() - 1) {
                supportedMakes.getAvStyleUnsupported().add(avitoMakeList.get(i));
            }
            if (i <= autoruMakeList.size() - 1) {
                supportedMakes.getAuStyleUnsupported().add(autoruMakeList.get(i));
            }
            if (i <= dromMakeList.size() - 1) {
                supportedMakes.getDrStyleUnsupported().add(dromMakeList.get(i));
            }

        }

        return supportedMakes;
    }


    @SafeVarargs
    private <T> List<T> findIntersection(List<T>... lists) {
        if (lists.length > 1) {
            List<T> result = Arrays.stream(lists).findFirst().get();
            for (List<T> list : lists) {
                result = CollectionUtils.intersection(result, list).stream().toList();
            }
            return result;
        }
        return List.of();
    }


    private void deleteFile(String fileName) {
        File file = new File(fileName);
        if (!file.delete() && file.isFile()) {
            System.out.println("Cannot delete file:" + fileName);
        }
    }


}
