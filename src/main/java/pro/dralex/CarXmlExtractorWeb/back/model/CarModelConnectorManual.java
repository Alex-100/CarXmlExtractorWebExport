package pro.dralex.CarXmlExtractorWeb.back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.dralex.CarXmlExtractorWeb.back.makes.ConnectorSource;

@Data
@Entity
@Table
@NoArgsConstructor
public class CarModelConnectorManual {
    @Id
    @GeneratedValue
    private Long id;
    private String make;
    private String modelGroup;
    private String avStyle;
    private String auStyle;
    private String drStyle;

    private ConnectorSource source;

    public CarModelConnectorManual(String make, String avStyle, String auStyle, String drStyle, ConnectorSource source) {
        this.make = make;
        this.modelGroup = avStyle;
        this.avStyle = avStyle;
        this.auStyle = auStyle;
        this.drStyle = drStyle;
        this.source = source;
    }
    public CarModelConnectorManual(String make, String avStyle, String auStyle, String drStyle) {
        this.make = make;
        this.modelGroup = avStyle;
        this.avStyle = avStyle;
        this.auStyle = auStyle;
        this.drStyle = drStyle;
        this.source = ConnectorSource.MANUAL;
    }
    public CarModelConnectorManual(String make, String modelGroup, String avStyle, String auStyle, String drStyle) {
        this.make = make;
        this.modelGroup = modelGroup;
        this.source = ConnectorSource.MANUAL;
        this.avStyle = avStyle;
        this.auStyle = auStyle;
        this.drStyle = drStyle;
    }
}
