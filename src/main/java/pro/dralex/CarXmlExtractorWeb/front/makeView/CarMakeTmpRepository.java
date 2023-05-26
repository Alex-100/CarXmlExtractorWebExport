package pro.dralex.CarXmlExtractorWeb.front.makeView;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.dralex.CarXmlExtractorWeb.front.makeView.CarMakeConnectorTmp;

import java.util.Optional;

public interface CarMakeTmpRepository extends JpaRepository<CarMakeConnectorTmp, Long> {

    @Query("SELECT connector FROM CarMakeConnectorTmp connector WHERE " +
            "connector.avStyle = ?1 " +
            "AND connector.auStyle = ?2 " +
            "AND connector.drStyle = ?3 "
    )
    Optional<CarMakeConnectorTmp> getByFields(String avStyle, String auStyle, String drStyle);
}
