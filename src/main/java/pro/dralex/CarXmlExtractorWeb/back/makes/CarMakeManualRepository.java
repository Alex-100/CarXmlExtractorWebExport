package pro.dralex.CarXmlExtractorWeb.back.makes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CarMakeManualRepository extends JpaRepository<CarMakeConnectorManual, Long> {

    @Query("SELECT connector FROM CarMakeConnectorManual connector WHERE " +
            "connector.avStyle = ?1 " +
            "AND connector.auStyle = ?2 " +
            "AND connector.drStyle = ?3 "
    )
    Optional<CarMakeConnectorManual> getByFields(String avStyle, String auStyle, String drStyle);
}
