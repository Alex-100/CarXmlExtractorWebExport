package pro.dralex.CarXmlExtractorWeb.back.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarModelRepository extends JpaRepository<CarModelConnector, Long> {
    List<CarModelConnector> findByMakeIgnoreCase(String make);

//    List<CarModelConnector> findByMake(String make);

}
