package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.entities.CountryRegionEntity;
import io.gupshup.mdb.exceptions.ResourceAlreadyExistsException;
import io.gupshup.mdb.exceptions.ResourceNotFountException;
import io.gupshup.mdb.repository.CountryRegionRepository;
import io.gupshup.mdb.service.CountryRegionService;
import io.gupshup.mdb.validator.MarketingDashboardValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import static io.gupshup.mdb.service.impl.ServiceCommonValidations.validateField;

@Service("CountryRegionService")
public class CountryRegionServiceImpl implements CountryRegionService {
    private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);


    @Autowired
    private CountryRegionRepository countryRegionRepository;

    @Override
    public void createRegion(String region) {
        logger.info("Request received for creating  region : {}", region);
        validateField(region, "region");
        Optional<CountryRegionEntity> entity=countryRegionRepository.findByRegion(region);
        entity.ifPresent(region1 -> {
            logger.info("Resource Already Exists : region: {}", region);
            throw new ResourceAlreadyExistsException("region", region);
        });
        CountryRegionEntity countryRegionEntity = new CountryRegionEntity();
        countryRegionEntity.setRegion(region);
        countryRegionRepository.save(countryRegionEntity);
    }

    @Override
    public void removeRegion(String region) {
        logger.info("Request received for creating  region : {}", region);
        validateField(region, "region");
        CountryRegionEntity entity=countryRegionRepository.findByRegion(region)
                .orElseThrow(() -> new ResourceNotFountException("Region", region));

        countryRegionRepository.delete(entity);
    }

}
