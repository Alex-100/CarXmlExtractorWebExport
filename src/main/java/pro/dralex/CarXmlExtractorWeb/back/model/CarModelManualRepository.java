package pro.dralex.CarXmlExtractorWeb.back.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CarModelManualRepository extends JpaRepository<CarModelConnectorManual, Long> {
    List<CarModelConnectorManual> findByMakeIgnoreCase(String make);

    @Query("SELECT conn FROM CarModelConnectorManual conn WHERE conn.avStyle = ?1 " +
                    "AND conn.auStyle = ?2 " +
                    "AND conn.drStyle = ?3 ")
    Optional<CarModelConnectorManual> findByFields(String avStyle, String auStyle, String drStyle);

}
