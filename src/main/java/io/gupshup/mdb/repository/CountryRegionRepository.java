package io.gupshup.mdb.repository;

import io.gupshup.mdb.entities.CountryRegionEntity;
import io.gupshup.mdb.entities.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRegionRepository extends JpaRepository<CountryRegionEntity,String> {
    Optional<CountryRegionEntity> findByRegion( String region);

}
