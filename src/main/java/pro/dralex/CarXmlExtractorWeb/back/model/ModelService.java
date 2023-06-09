package pro.dralex.CarXmlExtractorWeb.back.model;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;
import pro.dralex.CarXmlExtractorWeb.back.makes.CarMakeConnector;
import pro.dralex.CarXmlExtractorWeb.back.makes.CarMakeRepository;
import pro.dralex.CarXmlExtractorWeb.back.xml.ModelsFromXml;
import pro.dralex.CarXmlExtractorWeb.back.xml.XmlExtractor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModelService {
    private final CarMakeRepository carMakeRepository;
    private final CarModelRepository carModelRepository;
    private final CarModelManualRepository carModelmanualRepository;

    private final XmlExtractor xmlExtractor = new XmlExtractor();

    private Map<String, ModelsFromXml> models = new HashMap<>();

//    public List<CarModelConnector> getModels(String make) {
//        return modelRepository.findByMake(make);
//    }

    public Map<String, ModelsFromXml> getModelsFromXml() throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        List<CarMakeConnector> supportedMakes = carMakeRepository.findAll();
        return xmlExtractor.getXmlModels(supportedMakes);
    }

    public void saveManualModelsToDb(){
         carModelmanualRepository.saveAll(xmlExtractor.getManualRulesForModels());
    }

    public List<CarModelConnectorManual> getManualModelsFromDb(String make){
        return carModelmanualRepository.findByMakeIgnoreCase(make);
    }


    @Transactional
    public void updateManualModel(CarModelConnectorManual newData, Long oldId) {
        CarModelConnectorManual connectorManual = carModelmanualRepository.findById(oldId).orElseThrow();
        BeanUtils.copyProperties(newData, connectorManual, "id");
    }

    public Optional<CarModelConnectorManual> findByFields(String avStyle, String auStyle, String drStyle){
        return carModelmanualRepository.findByFields(avStyle, auStyle, drStyle);
    }
    public void addManualModel(CarModelConnectorManual newData) {
        carModelmanualRepository.save(newData);
    }
    public void hasManualModel(CarModelConnectorManual newData) {

        carModelmanualRepository.save(newData);
    }

    public void remove(CarModelConnectorManual selectedModelBlueprint) {
        carModelmanualRepository.delete(selectedModelBlueprint);
    }

    public boolean hasEmptyManualModels(){
        return carModelmanualRepository.findAll().size() == 0;
    }

    public void saveAllModelsToDb(List<CarModelConnector> result) {
        carModelRepository.deleteAll();
        carModelRepository.saveAll(result);
    }
    public List<CarModelConnector> getAllModelsFromDb() {
        return  carModelRepository.findAll();
    }
    public List<CarModelConnector> getAllModelsFromDb(String make) {
        return  carModelRepository.findByMakeIgnoreCase(make);
    }
}
