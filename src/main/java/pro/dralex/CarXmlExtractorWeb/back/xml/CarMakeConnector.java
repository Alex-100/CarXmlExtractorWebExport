package pro.dralex.CarXmlExtractorWeb.back.xml;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Table
@Entity
@NoArgsConstructor
@EqualsAndHashCode
public class CarMakeConnector {
    @Id
    @GeneratedValue
    private Long id;
    private String avStyle;
    private String auStyle;
    private String drStyle;

}
