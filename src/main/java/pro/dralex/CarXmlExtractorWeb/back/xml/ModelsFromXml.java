package pro.dralex.CarXmlExtractorWeb.back.xml;

import lombok.Data;
import pro.dralex.CarXmlExtractorWeb.back.model.CarModelConnectorManual;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModelsFromXml {
    private List<CarModelConnectorManual> supportedMakesXml = new ArrayList<>();
    private List<CarModelConnectorManual> supportedMakesManual = new ArrayList<>();
    private List<String> avStyleUnsupported = new ArrayList<>();
    private List<String> auStyleUnsupported = new ArrayList<>();
    private List<String> drStyleUnsupported = new ArrayList<>();

    public List<CarModelConnectorManual> getAll(){
        List<CarModelConnectorManual> carModelConnectorManuals = new ArrayList<>();
        carModelConnectorManuals.addAll(supportedMakesXml);
        carModelConnectorManuals.addAll(supportedMakesManual);
        return carModelConnectorManuals;
    }


}
