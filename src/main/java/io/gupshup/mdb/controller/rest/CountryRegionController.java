package io.gupshup.mdb.controller.rest;

import io.gupshup.mdb.auth.AuthenticationService;
import io.gupshup.mdb.dto.list.ContactCampaignList;
import io.gupshup.mdb.entities.CountryRegionEntity;
import io.gupshup.mdb.service.CountryRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.gupshup.mdb.constants.APIConstants.*;

@RestController
@RequestMapping(value = API + VERSION + ADMIN)
public class CountryRegionController {


    @Autowired
    private CountryRegionService countryRegionService;

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = ENABLE, method = RequestMethod.POST)
    public ResponseEntity<CountryRegionEntity> createRegion(@RequestParam(name="region") String region){
        countryRegionService.createRegion(region);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = DISABLE, method = RequestMethod.DELETE)
    public ResponseEntity<CountryRegionEntity> removeRegion(@RequestParam(name="region") String region){
        countryRegionService.removeRegion(region);
        return ResponseEntity.ok().build();
    }

}
