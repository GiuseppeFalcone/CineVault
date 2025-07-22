package com.falconevettorello.staticdataserver.movies;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.falconevettorello.staticdataserver.genres.Genre;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByReleasesReleaseDateBetween(LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT m.* FROM movies m " +
            "LIMIT 20 OFFSET :offset", nativeQuery = true)
    List<Movie> find20Movies(@Param("offset") int offset);

    List<Movie> findByTitleIn(List<String> movieTitles);

    @Query("SELECT DISTINCT m FROM Movie m " +
            "LEFT JOIN m.genres g " +
            "LEFT JOIN m.languages l " +
            "LEFT JOIN m.productionCountries pc " +
            "WHERE (:genre IS NULL OR g.genre = :genre) " +
            "AND (:language IS NULL OR l.language = :language) " +
            "AND (:country IS NULL OR pc.country = :country) " +
            "AND (:releaseYear IS NULL OR m.year = :releaseYear) " +
            "ORDER BY m.year DESC")
    List<Movie> findMoviesByFilters(@Param("genre") String genre,
                                    @Param("language") String language,
                                    @Param("country") String country,
                                    @Param("releaseYear") Year releaseYear,
                                    Pageable pageable);

    @Query("SELECT DISTINCT m FROM Movie m " +
            "LEFT JOIN m.genres g " +
            "LEFT JOIN m.languages l " +
            "LEFT JOIN m.productionCountries pc " +
            "WHERE (:genre IS NULL OR g.genre = :genre) " +
            "AND (:language IS NULL OR l.language = :language) " +
            "AND (:country IS NULL OR pc.country = :country) " +
            "AND (:releaseYear IS NULL OR m.year = :releaseYear) " +
            "AND (:titles IS NULL OR LOWER(m.title) IN :titles) " +
            "ORDER BY m.year DESC")
    List<Movie> findMoviesByFiltersAndTitles(@Param("genre") String genre,
                                             @Param("language") String language,
                                             @Param("country") String country,
                                             @Param("releaseYear") Year releaseYear,
                                             @Param("titles") List<String> titles,
                                             Pageable pageable);

    @Query("SELECT DISTINCT m.year " +
            "FROM Movie m " +
            "WHERE m.year <= FUNCTION('date_part', 'year', CURRENT_DATE) " +
            "ORDER BY m.year DESC")
    List<Year> findUniqueReleaseYears();

    List<Movie> findByTitleContainingIgnoreCaseOrderByIdAsc(String title, Pageable pageable);

    List<Movie> findByYearOrderByIdAsc(Year year, Pageable pageable);

    List<Movie> findDistinctByLanguagesLanguageContainingIgnoreCaseAndLanguagesTypeNotIgnoreCaseOrderByIdAsc(String language, String type, Pageable pageable);

    List<Movie> findDistinctByProductionCountriesCountryContainingIgnoreCaseOrderByIdAsc(String productionCountry, Pageable pageable);

    List<Movie> findDistinctByStudiosStudioContainingIgnoreCaseOrderByIdAsc(String studio, Pageable pageable);

    List<Movie> findDistinctByThemesThemeContainingIgnoreCaseOrderByIdAsc(String theme, Pageable pageable);

    List<Movie> findDistinctByGenresGenreContainingIgnoreCaseOrderByIdAsc(String genre, Pageable pageable);

    List<Movie> findDistinctByCrewCrewNameContainingIgnoreCaseOrderByIdAsc(String crew, Pageable pageable);

    List<Movie> findDistinctByActorsActorNameContainingIgnoreCaseOrderByIdAsc(String actor, Pageable pageable);

    List<Movie> findTop5ByGenresGenreAndIdNot(String genre, Long id);

    List<Movie> findMoviesByActorsActorNameEqualsIgnoreCaseOrderByIdAsc(String actor, Pageable pageable);

    List<Movie> findMoviesByCrewCrewNameEqualsIgnoreCaseOrderByIdAsc(String crew, Pageable pageable);

    boolean existsByActorsActorNameEqualsIgnoreCase(String personName);

    boolean existsByCrewCrewNameEqualsIgnoreCase(String personName);

    List<Movie> findTop10ByGenresGenreInAndIdNot(Set<String> genres, Long movieId);

    int countByGenresGenre(String genres);

    List<Movie> findByGenresGenreOrderByIdAsc(String genre, Pageable pageable);
}