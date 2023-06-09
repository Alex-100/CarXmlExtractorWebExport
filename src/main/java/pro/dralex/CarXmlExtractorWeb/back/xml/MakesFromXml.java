package pro.dralex.CarXmlExtractorWeb.back.xml;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pro.dralex.CarXmlExtractorWeb.back.makes.CarMakeConnectorManual;

import java.util.ArrayList;
import java.util.List;


@Data
@RequiredArgsConstructor
public class MakesFromXml {
    private List<CarMakeConnectorManual> supportedMakes = new ArrayList<>();
    private List<String> avStyleUnsupported = new ArrayList<>();
    private List<String> auStyleUnsupported = new ArrayList<>();
    private List<String> drStyleUnsupported = new ArrayList<>();
}
