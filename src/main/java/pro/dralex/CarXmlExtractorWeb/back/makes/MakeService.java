package pro.dralex.CarXmlExtractorWeb.back.makes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import pro.dralex.CarXmlExtractorWeb.back.fileLoader.FileLoader;
import pro.dralex.CarXmlExtractorWeb.back.xml.MakesFromXml;
import pro.dralex.CarXmlExtractorWeb.back.xml.XmlExtractor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MakeService {
    private final CarMakeManualRepository carMakeManualRepository;

    private final CarMakeRepository carMakeRepository;

    public List<CarMakeConnector> getSupportedMakes(){
        return carMakeRepository.findAll();
    }

    public List<CarMakeConnectorManual> getManualMakes(){
        return carMakeManualRepository.findAll();
    }

    public void deleteSupportedMake(Long id) {
        carMakeRepository.deleteById(id);
    }

    public void deleteAllSupportedMakes() {
        carMakeRepository.deleteAll();
    }

    public void saveAllSupportedMakes(List<CarMakeConnector> connectors) {
        carMakeRepository.saveAll(connectors);
    }

    public Optional<CarMakeConnector> findSupportedById(Long id) {
        return carMakeRepository.findById(id);
    }
    public void saveSupportedMake(CarMakeConnector connector) {
        carMakeRepository.save(connector);
    }
    public boolean isManualMakePresent(String avText, String auText, String drText) {
        return carMakeManualRepository.getByFields(auText, avText, drText).isPresent();
    }

    public void saveManualMake(CarMakeConnectorManual connectorManual) {
        carMakeManualRepository.save(connectorManual);
    }

    public Optional<CarMakeConnectorManual> findManualMakeById(Long id) {
        return carMakeManualRepository.findById(id);
    }
    public void deleteManualMakeById(Long id) {
        carMakeManualRepository.deleteById(id);
    }

    public MakesFromXml getAllMakesFromXml() throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        XmlExtractor xmlExtractor = new XmlExtractor();
        return xmlExtractor.getAllMakesFromXml(FileLoader.avFileName, FileLoader.auFileName, FileLoader.drFileName);
    }

}
