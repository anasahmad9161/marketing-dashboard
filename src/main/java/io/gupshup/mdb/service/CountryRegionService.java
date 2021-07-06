package io.gupshup.mdb.service;

import io.gupshup.mdb.entities.CountryRegionEntity;

public interface CountryRegionService {

    void createRegion(String region);

    void removeRegion(String region);
}
