package com.falconevettorello.staticdataserver.languages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    @Query("SELECT l.language FROM Language l GROUP BY l.language ORDER BY COUNT(l) DESC")
    List<String> findDistinctLanguagesByFrequency();
}
