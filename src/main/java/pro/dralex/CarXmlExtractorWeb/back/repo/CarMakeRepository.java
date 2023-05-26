package pro.dralex.CarXmlExtractorWeb.back.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.dralex.CarXmlExtractorWeb.back.xml.CarMakeConnector;

public interface CarMakeRepository extends JpaRepository<CarMakeConnector, Long> {
}
