package pro.dralex.CarXmlExtractorWeb.back.xml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import pro.dralex.CarXmlExtractorWeb.back.fileLoader.FileLoader;
import pro.dralex.CarXmlExtractorWeb.back.makes.CarMakeConnector;
import pro.dralex.CarXmlExtractorWeb.back.makes.CarMakeConnectorManual;
import pro.dralex.CarXmlExtractorWeb.back.makes.ConnectorSource;
import pro.dralex.CarXmlExtractorWeb.back.model.CarModelConnectorManual;

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
    public static class Car {
        private String make;
        private String model;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class DrCarMake {
        private int id;
        private String make;
    }

    private List<Car> getDrCarsFromXml(String fileName) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        List<Car> result = new ArrayList<>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            doc.getDocumentElement().normalize();

            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList makesList = (NodeList) xPath.compile("/omne/two").evaluate(doc, XPathConstants.NODESET);
            log.info("drom total models size:{}", makesList.getLength());
            List<DrCarMake> dromCarMakes = new ArrayList<>();
            for (int i = 0; i <= makesList.getLength() - 1; i++) {
                Element elementMark = (Element) makesList.item(i);
                String idMark = elementMark.getElementsByTagName("idMark").item(0).getTextContent();
                String make = elementMark.getElementsByTagName("sMark").item(0).getTextContent();
                dromCarMakes.add(new DrCarMake(Integer.parseInt(idMark), make));
            }

            NodeList modelsList = (NodeList) xPath.compile("/one/twi").evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i <= modelsList.getLength() - 1; i++) {
                Element elementMark = (Element) modelsList.item(i);
                int makeId = 100;
                String model = "smodel";
                String make = dromCarMakes.stream()
                        .filter(car -> car.id == makeId)
                        .findFirst()
                        .orElse(new DrCarMake(-1, ""))
                        .getMake();
                result.add(new Car(make, model));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Car> getAvCarsFromXml(String fileName) {
        List<Car> result = new ArrayList<>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            doc.getDocumentElement().normalize();

            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.compile("/one/two").evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i <= nodeList.getLength() - 1; i++) {
                Node model = nodeList.item(i);
                result.add(new Car(model.toString(), ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Car> getAuCarsFromXml(String fileName) {
        List<Car> result = new ArrayList<>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            doc.getDocumentElement().normalize();

            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.compile("/one/two").evaluate(doc, XPathConstants.NODESET);
            log.info("");
            for (int i = 0; i <= nodeList.getLength() - 1; i++) {
                Node model = nodeList.item(i);
                result.add(new Car(model.toString(), ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public MakesFromXml getAllMakesFromXml(String avFileName, String auFilename, String drFileName) throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        List<Car> avitoCars = getAvCarsFromXml(avFileName);
        List<Car> autoruCars = getAuCarsFromXml(auFilename);
        List<Car> dromCars = getDrCarsFromXml(drFileName);
        return getAllMakes(avitoCars, autoruCars, dromCars);
    }

    private MakesFromXml getAllMakes(List<Car> avCars,
                                     List<Car> auCars,
                                     List<Car> drCars) {

        List<String> avMakeList = new ArrayList<>(avCars.stream()
                .map(Car::getMake)
                .distinct()
                .sorted()
                .toList());

        List<String> auMakeList = new ArrayList<>(auCars.stream()
                .map(Car::getMake)
                .distinct()
                .sorted()
                .toList());

        List<String> drMakeList = new ArrayList<>(drCars.stream()
                .map(Car::getMake)
                .distinct()
                .sorted()
                .toList());


        MakesFromXml supportedMakes = new MakesFromXml();
        List<String> makes = findIntersection(avMakeList, auMakeList, drMakeList);
        makes.stream().sorted().forEach(make -> {
            avMakeList.remove(make);
            auMakeList.remove(make);
            drMakeList.remove(make);
            supportedMakes.getSupportedMakes().add(new CarMakeConnectorManual(make, make, make, ConnectorSource.AUTO_XML));
        });


        List<CarMakeConnectorManual> rulesAutomatic = new ArrayList<>();
        List<String> avitoModelsTmp = new ArrayList<>(avMakeList);
        avitoModelsTmp.forEach(avitoModelTmp -> {
                    Optional<String> autoruModel = auMakeList.stream().filter(item -> item.equalsIgnoreCase(avitoModelTmp)).findFirst();
                    Optional<String> dromModel = drMakeList.stream().filter(item -> item.equalsIgnoreCase(avitoModelTmp)).findFirst();
                    if (autoruModel.isPresent() && dromModel.isPresent()) {
                        rulesAutomatic.add(new CarMakeConnectorManual(avitoModelTmp, autoruModel.get(), dromModel.get(), ConnectorSource.AUTO_XML));
                        avMakeList.remove(avitoModelTmp);
                        auMakeList.remove(autoruModel.get());
                        drMakeList.remove(dromModel.get());
                    }
                }
        );

        rulesAutomatic.forEach(model -> {
            supportedMakes.getSupportedMakes().add(
                    new CarMakeConnectorManual(
                            model.getAvStyle(),
                            model.getAuStyle(),
                            model.getDrStyle(),
                            ConnectorSource.AUTO_XML)
            );
        });

        int maxIndex = Collections.max(List.of(avMakeList.size(), auMakeList.size(), drMakeList.size()));
        for (int i = 0; i < maxIndex; i++) {
            if (i <= avMakeList.size() - 1) {
                supportedMakes.getAvStyleUnsupported().add(avMakeList.get(i));
            }
            if (i <= auMakeList.size() - 1) {
                supportedMakes.getAuStyleUnsupported().add(auMakeList.get(i));
            }
            if (i <= drMakeList.size() - 1) {
                supportedMakes.getDrStyleUnsupported().add(drMakeList.get(i));
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

    public Map<String, ModelsFromXml> getXmlModels(List<CarMakeConnector> supportedMakes) throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        List<Car> avCars = getAvCarsFromXml(FileLoader.avFileName);
        List<Car> auCars = getAuCarsFromXml(FileLoader.auFileName);
        List<Car> drCars = getDrCarsFromXml(FileLoader.drFileName);
        return getModelsFromXml(avCars, auCars, drCars, supportedMakes);

    }

    private Map<String, ModelsFromXml> getModelsFromXml(List<Car> avitoCars,
                                                        List<Car> autoruCars,
                                                        List<Car> dromCars,
                                                        List<CarMakeConnector> supportedMakes) {

        Map<String, ModelsFromXml> result = new HashMap<>();

        for (CarMakeConnector make : supportedMakes) {

            ModelsFromXml modelsFromXml = new ModelsFromXml();

            List<String> avModels = new ArrayList<>(avitoCars.stream()
                    .filter(car -> car.getMake().equalsIgnoreCase(make.getAvStyle()))
                    .map(Car::getModel)
                    .distinct()
                    .sorted()
                    .toList());

            List<String> auModels = new ArrayList<>(autoruCars.stream()
                    .filter(car -> car.getMake().equalsIgnoreCase(make.getAuStyle()))
                    .map(Car::getModel)
                    .sorted()
                    .distinct()
                    .toList());

            List<String> drModels = new ArrayList<>(dromCars.stream()
                    .filter(car -> car.getMake().equalsIgnoreCase(make.getDrStyle()))
                    .map(Car::getModel)
                    .sorted()
                    .distinct()
                    .toList());


            List<String> avitoModelsTmp = new ArrayList<>(avModels);
            avitoModelsTmp.forEach(avModelTmp -> {
                        Optional<String> auModelOpt = auModels.stream().filter(item -> item.equalsIgnoreCase(avModelTmp)).findFirst();
                        Optional<String> drModelOpt = drModels.stream().filter(item -> item.equalsIgnoreCase(avModelTmp)).findFirst();
                        if (auModelOpt.isPresent() && drModelOpt.isPresent()) {
                            modelsFromXml.getSupportedMakesXml().add(
                                    new CarModelConnectorManual(
                                            make.getAvStyle(),
                                            avModelTmp,
                                            auModelOpt.get(),
                                            drModelOpt.get(),
                                            ConnectorSource.AUTO_XML)
                            );
                            avModels.remove(avModelTmp);
                            auModels.remove(auModelOpt.get());
                            drModels.remove(drModelOpt.get());
                        }
                    }
            );

            int maxIndex = Collections.max(List.of(avModels.size(), auModels.size(), drModels.size()));
            for (int i = 0; i < maxIndex; i++) {
                if (i <= avModels.size() - 1) {
                    modelsFromXml.getAvStyleUnsupported().add(avModels.get(i));
                }
                if (i <= auModels.size() - 1) {
                    modelsFromXml.getAuStyleUnsupported().add(auModels.get(i));
                }
                if (i <= drModels.size() - 1) {
                    modelsFromXml.getDrStyleUnsupported().add(drModels.get(i));
                }
            }
            result.put(make.getAvStyle().toUpperCase(), modelsFromXml);
        }
        return result;
    }



    public List<CarModelConnectorManual> getManualRulesForModels() {
        return new ArrayList<>();
    }


}
