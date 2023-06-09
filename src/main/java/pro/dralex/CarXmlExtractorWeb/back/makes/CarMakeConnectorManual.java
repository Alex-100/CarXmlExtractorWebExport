package pro.dralex.CarXmlExtractorWeb.back.makes;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table

public class CarMakeConnectorManual {
    @Id
    @GeneratedValue
    private Long id;
    private String avStyle;
    private String auStyle;
    private String drStyle;

    private ConnectorSource source;

    public CarMakeConnectorManual(String avStyle, String auStyle, String drStyle, ConnectorSource source) {
        this.avStyle = avStyle;
        this.auStyle = auStyle;
        this.drStyle = drStyle;
        this.source = source;
    }

}
