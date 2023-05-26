package pro.dralex.CarXmlExtractorWeb.back.xml;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pro.dralex.CarXmlExtractorWeb.front.makeView.CarMakeConnectorTmp;

import java.util.ArrayList;
import java.util.List;


@Data
@RequiredArgsConstructor
public class MakesResultFromXml {
    private List<CarMakeConnectorTmp> supportedMakes = new ArrayList<>();
    private List<String> avStyleUnsupported = new ArrayList<>();
    private List<String> auStyleUnsupported = new ArrayList<>();
    private List<String> drStyleUnsupported = new ArrayList<>();
}
