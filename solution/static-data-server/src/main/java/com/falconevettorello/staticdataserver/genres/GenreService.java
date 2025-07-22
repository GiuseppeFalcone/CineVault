package com.falconevettorello.staticdataserver.genres;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling genre-related operations.
 * This service interacts with the GenreRepository to fetch distinct genres.
 */
@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    /**
     * Retrieves a list of unique genres from the repository.
     *
     * @return a List of Strings representing the distinct genres.
     */
    public List<String> getDistinctGenres() {
        return genreRepository.findDistinctGenres();
    }
}
