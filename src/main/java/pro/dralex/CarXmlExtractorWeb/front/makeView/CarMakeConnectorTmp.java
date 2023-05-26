package pro.dralex.CarXmlExtractorWeb.front.makeView;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.dralex.CarXmlExtractorWeb.back.xml.ConnectorSource;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table

public class CarMakeConnectorTmp {
    @Id
    @GeneratedValue
    private Long id;
    private String avStyle;
    private String auStyle;
    private String drStyle;

    private ConnectorSource source;

    public CarMakeConnectorTmp(String avStyle, String auStyle, String drStyle, ConnectorSource source) {
        this.avStyle = avStyle;
        this.auStyle = auStyle;
        this.drStyle = drStyle;
        this.source = source;
    }

}
