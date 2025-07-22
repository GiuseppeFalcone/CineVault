package com.falconevettorello.staticdataserver.productionCountries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling production country-related operations.
 * This service interacts with the ProductionCountryRepository to fetch unique production countries.
 */
@Service
public class ProductionCountryService {

    @Autowired
    private ProductionCountryRepository productionCountryRepository;

    /**
     * Retrieves a list of distinct production countries.
     *
     * @return a List of Strings representing the unique production countries.
     */
    public List<String> getDistinctProductionCountries() {
        return productionCountryRepository.findDistinctProductionCountriesByFrequency();
    }
}
