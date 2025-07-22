package com.falconevettorello.staticdataserver.languages;

import com.falconevettorello.staticdataserver.movies.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling language-related operations.
 * This service interacts with the LanguageRepository to fetch unique languages.
 */
@Service
public class LanguageService {

    @Autowired
    private LanguageRepository languageRepository;

    /**
     * Retrieves a list of distinct languages.
     *
     * @return a List of Strings representing the unique languages.
     */
    public List<String> getDistinctLanguages() {
        return languageRepository.findDistinctLanguagesByFrequency();
    }


    public List<LanguageDTO> convertToLanguageDTO(List<Language> languages) {
        return languages.stream()
                .map(language -> new LanguageDTO(language.getLanguage(), language.getType()))
                .toList();
    }
}
