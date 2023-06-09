package pro.dralex.CarXmlExtractorWeb.back.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table
@NoArgsConstructor
public class CarModelConnector {
    @Id
    @GeneratedValue
    private Long id;
    private String make;
    private String modelGroup;
    private String avStyle;
    private String auStyle;
    private String drStyle;

    public CarModelConnector(String make, String modelGroup, String avStyle, String auStyle, String drStyle) {
        this.make = make;
        this.modelGroup = modelGroup;
        this.avStyle = avStyle;
        this.auStyle = auStyle;
        this.drStyle = drStyle;
    }
}
