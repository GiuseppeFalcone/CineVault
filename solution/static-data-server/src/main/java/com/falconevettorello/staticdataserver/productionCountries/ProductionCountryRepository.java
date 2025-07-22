package com.falconevettorello.staticdataserver.productionCountries;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionCountryRepository extends JpaRepository<ProductionCountry, Long> {
    @Query("SELECT pc.country " +
            "FROM ProductionCountry pc " +
            "GROUP BY pc.country " +
            "ORDER BY COUNT(pc) DESC")
    List<String> findDistinctProductionCountriesByFrequency();
}
