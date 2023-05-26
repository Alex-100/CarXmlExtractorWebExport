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
    private static class DrCarMake {
        private int id;
        private String make;
    }

    private List<Car> getDrCarsFromXml(String fileName) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        List<Car> result = new ArrayList<>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        doc.getDocumentElement().normalize();

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList makesList = (NodeList) xPath.compile("/Xml/Xml/Xml").evaluate(doc, XPathConstants.NODESET);
        log.info("dr total models size:{}", makesList.getLength());
        List<DrCarMake> drCarMakes = new ArrayList<>();
        for (int i = 0; i <= makesList.getLength() - 1; i++) {
            Element elementMark = (Element) makesList.item(i);
            String idMark = elementMark.getElementsByTagName("one").item(0).getTextContent();
            String make = elementMark.getElementsByTagName("two").item(0).getTextContent();
            drCarMakes.add(new DrCarMake(Integer.parseInt(idMark), make));
        }

        NodeList modelsList = (NodeList) xPath.compile("/Xml/Xml/Xml").evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i <= modelsList.getLength() - 1; i++) {
            Element elementMark = (Element) modelsList.item(i);
            int makeId = Integer.parseInt(elementMark.getElementsByTagName("one").item(0).getTextContent());
            String model = elementMark.getElementsByTagName("two").item(0).getTextContent();
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
        NodeList nodeList = (NodeList) xPath.compile("/xml/xml").evaluate(doc, XPathConstants.NODESET);
        log.info("av total model size:{}", nodeList.getLength());
        log.info("");
        for (int i = 0; i <= nodeList.getLength() - 1; i++) {
            Node model = nodeList.item(i);
            NamedNodeMap modelDetail = model.getAttributes();
            Node make = model.getParentNode();
            NamedNodeMap makeDetail = make.getAttributes();
            String modelStr = modelDetail.getNamedItem("one").getNodeValue();
            String makeStr = makeDetail.getNamedItem("two").getNodeValue();
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
        NodeList nodeList = (NodeList) xPath.compile("/xml/xml").evaluate(doc, XPathConstants.NODESET);
        log.info("au total model size:{}", nodeList.getLength());
        log.info("");
        for (int i = 0; i <= nodeList.getLength() - 1; i++) {
            Node model = nodeList.item(i);
            NamedNodeMap modelDetail = model.getAttributes();
            Node make = model.getParentNode();
            NamedNodeMap makeDetail = make.getAttributes();
            String modelStr = modelDetail.getNamedItem("one").getNodeValue().replaceAll(",.+", "");
            String makeStr = makeDetail.getNamedItem("two").getNodeValue();
            result.add(new Car(makeStr, modelStr));
        }

        return result;
    }


    public MakesResultFromXml getSupportedMakes(String avFileName, String auFilename, String drFileName) throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        List<Car> avitoCars = getAvCarsFromXml(avFileName);
        List<Car> autoruCars = getAuCarsFromXml(auFilename);
        List<Car> dromCars = getDrCarsFromXml(drFileName);
        return showSupportedMakes(avitoCars, autoruCars, dromCars);
    }

    private MakesResultFromXml showSupportedMakes(List<Car> avitoCars,
                                                  List<Car> autoruCars,
                                                  List<Car> dromCars
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



}
