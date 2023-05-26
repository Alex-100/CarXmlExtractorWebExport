package pro.dralex.CarXmlExtractorWeb.back.url;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    final UrlRepository urlRepository;

    public UrlContainer getContainer() {
        Optional<UrlContainer> urlContainerOptional = urlRepository.findById(1);
        return urlContainerOptional.orElseGet(() -> urlRepository.save(new UrlContainer()));
    }

    @Transactional
    public void updateContainer(UrlContainer urlContainerNew) {
        UrlContainer urlContainer = urlRepository.findById(1).orElseGet(UrlContainer::new);
        BeanUtils.copyProperties(urlContainerNew, urlContainer);
    }

}
