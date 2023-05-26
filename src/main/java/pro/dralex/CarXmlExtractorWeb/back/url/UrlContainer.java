package pro.dralex.CarXmlExtractorWeb.back.url;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table
public class UrlContainer {

    @Id
    private Integer id = 1;
    private String avStyleUrl = "";
    private String auStyleUrl = "";
    private String drStyleUrl = "";
}
