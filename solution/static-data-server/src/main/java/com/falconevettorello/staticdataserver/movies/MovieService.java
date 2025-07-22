package com.falconevettorello.staticdataserver.movies;

import com.falconevettorello.staticdataserver.exceptions.*;
import com.falconevettorello.staticdataserver.genres.Genre;
import com.falconevettorello.staticdataserver.genres.GenreService;
import com.falconevettorello.staticdataserver.languages.LanguageService;
import com.falconevettorello.staticdataserver.productionCountries.ProductionCountry;
import com.falconevettorello.staticdataserver.productionCountries.ProductionCountryService;
import com.falconevettorello.staticdataserver.movies.dto.*;
import com.falconevettorello.staticdataserver.actors.ActorService;
import com.falconevettorello.staticdataserver.crew.CrewService;
import com.falconevettorello.staticdataserver.studios.Studio;
import com.falconevettorello.staticdataserver.themes.Theme;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing business logic related to movies.
 * This class implements operations for searching, filtering, and transforming entities into DTOs,
 * keeping the controller free from business logic.
 */
@Service
public class MovieService {

    private static final int PAGINATION_LIMIT = 20;

    private final MovieRepository movieRepository;
    private final GenreService genreService;
    private final LanguageService languageService;
    private final ProductionCountryService productionCountryService;
    private final CrewService crewService;
    private final ActorService actorService;
    /**
     * Constructor for MovieService.
     * Autowired dependencies are injected to handle movie-related operations.
     *
     * @param movieRepository the repository for accessing movie data.
     * @param genreService service for managing genres.
     * @param languageService service for managing languages.
     * @param productionCountryService service for managing production countries.
     * @param crewService service for managing crew members.
     * @param actorService service for managing actors.
     */

    public MovieService(MovieRepository movieRepository,
                        GenreService genreService,
                        LanguageService languageService,
                        ProductionCountryService productionCountryService,
                        CrewService crewService,
                        ActorService actorService) {
        this.movieRepository = movieRepository;
        this.genreService = genreService;
        this.languageService = languageService;
        this.productionCountryService = productionCountryService;
        this.crewService = crewService;
        this.actorService = actorService;
    }

    /**
     * Retrieves a movie by its ID.
     * Throws a MovieNotFoundException if the movie is not found.
     *
     * @param id the movie's ID.
     * @return the movie entity.
     */
    @Transactional
    public MovieDetailedDTO getMovieById(Long id) {
        Optional<Movie> movieOptional = movieRepository.findById(id);
        if (movieOptional.isEmpty()) {
            throw new MovieNotFoundException("Movie not found with id: " + id);
        }
        return convertToDetailedDTO(movieOptional).orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
    }

    /**
     * Retrieves random movies grouped by genre.
     *
     * @return a list of movie categories based on genres.
     */
    public List<MoviesCategoryBasedDTO> getRandomMoviesGenreBased() {
        List<String> allGenres = genreService.getDistinctGenres();
        Collections.shuffle(allGenres);
        List<String> genres = allGenres.stream().limit(5).collect(Collectors.toList());
        List<MoviesCategoryBasedDTO> movieCategories = new ArrayList<>();
        for (String genre : genres) {
            int limit = 16;
            Pageable pageable = PageRequest.of(0, limit);

            List<Movie> movies = movieRepository.findByGenresGenreOrderByIdAsc(genre, pageable);
            if (!movies.isEmpty()) {
                List<MovieSummaryDTO> movieSummaryDTOs = movies.stream()
                        .map(this::convertToSummaryDTO)
                        .collect(Collectors.toList());
                movieCategories.add(new MoviesCategoryBasedDTO(genre, movieSummaryDTOs));
            }
        }
        if (movieCategories.isEmpty()) {
            throw new MovieNotFoundException("No movies found for the selected genres.");
        }
        // Sort categories by genre name for consistency.
        movieCategories.sort(Comparator.comparing(MoviesCategoryBasedDTO::getCategory, String.CASE_INSENSITIVE_ORDER));
        return movieCategories;
    }

    /**
     * Retrieves the latest movies (released within the last month).
     *
     * @return a category DTO containing the latest movies.
     */
    public MoviesCategoryBasedDTO getLatestMovies() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);
        List<Movie> latestMovies = movieRepository.findByReleasesReleaseDateBetween(startDate, endDate);
        List<MovieSummaryDTO> movieSummaryDTOs = latestMovies.stream()
                .map(this::convertToSummaryDTO)
                .distinct()
                .collect(Collectors.toList());
        return new MoviesCategoryBasedDTO("Latest", movieSummaryDTOs);
    }

    /**
     * Retrieves a paginated list of movies, with a limit of 20 movies per page.
     *
     * @param offset pagination offset.
     * @return a list of movie summaries.
     */
    public List<MovieSummaryDTO> findPaginatedMovies(int offset) {
        List<Movie> movies = movieRepository.find20Movies(offset);
        return movies.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves movies filtered by a list of titles.
     *
     * @param movieTitles list of movie titles.
     * @return a list of movie summaries.
     */
    public List<MovieSummaryDTO> getMoviesByTitles(List<String> movieTitles) {
        List<Movie> movies = movieRepository.findByTitleIn(movieTitles);
        return movies.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves movies filtered based on the provided parameters.
     *
     * @param filter filter criteria.
     * @return a list of movie summaries.
     */
    public List<MovieSummaryDTO> getFilteredMovies(MovieFilterRequest filter) {
        String genre = filter.getGenre();
        String language = filter.getLanguage();
        String country = filter.getCountry();
        Integer releaseYear = filter.getReleaseYear();
        Integer offset = filter.getOffset();
        Year releaseYearParsed = null;
        List<String> movieTitles = filter.getMovieTitles();
        if (releaseYear != null) {
            try {
                releaseYearParsed = Year.of(releaseYear);
            } catch (NumberFormatException e) {
                System.err.println("Invalid year format '" + releaseYear + "'. Year filter will be ignored.");
            }
        }
        // Normalize empty strings to null.
        genre = (genre != null && genre.trim().isEmpty()) ? null : genre;
        language = (language != null && language.trim().isEmpty()) ? null : language;
        country = (country != null && country.trim().isEmpty()) ? null : country;

        int page = offset / PAGINATION_LIMIT;
        Pageable pageable = PageRequest.of(page, PAGINATION_LIMIT);

        List<Movie> movies;
        if (movieTitles != null && !movieTitles.isEmpty()) {
            // Convert titles to lowercase for case-insensitive search.
            List<String> lowerCaseTitles = movieTitles.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            movies = movieRepository.findMoviesByFiltersAndTitles(genre, language, country, releaseYearParsed, lowerCaseTitles, pageable);
        } else {
            movies = movieRepository.findMoviesByFilters(genre, language, country, releaseYearParsed, pageable);
        }

        return movies.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves unique filters available for movies such as genres, languages, countries, and release years.
     *
     * @return a map where each key is a filter name and the value is a list of unique filter values.
     */
    public Map<String, List<String>> getUniqueFilters() {
        Map<String, List<String>> filters = new HashMap<>();
        filters.put("genres", genreService.getDistinctGenres());
        filters.put("languages", languageService.getDistinctLanguages());
        filters.put("countries", productionCountryService.getDistinctProductionCountries());
        List<String> releaseYears = movieRepository.findUniqueReleaseYears().stream()
                .map(Year::toString)
                .collect(Collectors.toList());
        filters.put("releaseYears", releaseYears);
        return filters;
    }

    /**
     * Searches for movies based on a specific field.
     * Validates the parameters and throws exceptions if the values are invalid.
     *
     * @param field  the field to search by (e.g., title, year, language).
     * @param value  the value to search for.
     * @param offset pagination offset.
     * @return a list of movie summaries that match the search criteria.
     */
    public List<MovieSummaryDTO> search(String field, String value, int offset) {
        if (field == null || field.trim().isEmpty()) {
            throw new WrongParamException("Invalid search field: " + field);
        }
        if (value == null || value.trim().isEmpty()) {
            throw new WrongParamException("Invalid search value: " + value);
        }

        Pageable pageable = PageRequest.of(offset / PAGINATION_LIMIT, PAGINATION_LIMIT);
        List<Movie> movies;
        field = field.trim().toLowerCase();
        value = value.trim().toLowerCase();
        switch (field.toLowerCase()) {
            case "title":
                movies = movieRepository.findByTitleContainingIgnoreCaseOrderByIdAsc(value, pageable);
                break;
            case "year":
                try {
                    Year year = Year.parse(value.trim());
                    movies = movieRepository.findByYearOrderByIdAsc(year, pageable);
                } catch (Exception e) {
                    throw new WrongParamException("Invalid search value for Year: " + value);
                }
                break;
            case "language":
                movies = movieRepository.findDistinctByLanguagesLanguageContainingIgnoreCaseAndLanguagesTypeNotIgnoreCaseOrderByIdAsc(value, "Spoken Language", pageable);
                break;
            case "productioncountry":
                movies = movieRepository.findDistinctByProductionCountriesCountryContainingIgnoreCaseOrderByIdAsc(value, pageable);
                break;
            case "studio":
                movies = movieRepository.findDistinctByStudiosStudioContainingIgnoreCaseOrderByIdAsc(value, pageable);
                break;
            case "theme":
                movies = movieRepository.findDistinctByThemesThemeContainingIgnoreCaseOrderByIdAsc(value, pageable);
                break;
            case "genre":
                movies = movieRepository.findDistinctByGenresGenreContainingIgnoreCaseOrderByIdAsc(value, pageable);
                break;
            case "crew":
                movies = movieRepository.findDistinctByCrewCrewNameContainingIgnoreCaseOrderByIdAsc(value, pageable);
                break;
            case "actor":
                movies = movieRepository.findDistinctByActorsActorNameContainingIgnoreCaseOrderByIdAsc(value, pageable);
                break;
            default:
                throw new MovieNotFoundException("No Movies found with given " + field + ": " + value);
        }

        return movies.stream()
                .sorted(Comparator.comparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER))
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves movies related to the specified movie based on shared genres.
     *
     * @param movieId the ID of the movie.
     * @return a list of movie summaries for related movies.
     */
    @Transactional
    public List<MovieSummaryDTO> getRelatedMovies(Long movieId) {
        // Check if the movie exists
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + movieId));
        // Get genres of the movie
        Set<String> genres = movie.getGenres().stream()
            .map(Genre::getGenre)
            .collect(Collectors.toSet());
        if (genres.isEmpty()) {
            return Collections.emptyList();
        }
        // Find up to 10 movies (excluding the current movie) that share any of the genres
        List<Movie> relatedMovies = movieRepository.findTop10ByGenresGenreInAndIdNot(genres, movieId);
        return relatedMovies.stream()
            .map(this::convertToSummaryDTO)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves movies associated with a person based on the type (actor or crew) and name.
     * Validates the parameters and throws exceptions in case of errors.
     *
     * @param personType the type of person ("actor" or "crew").
     * @param personName the name of the person.
     * @param offset     pagination offset.
     * @return a list of movie summaries.
     */
    public List<MovieSummaryDTO> getMoviesByName(String personType, String personName, int offset) {
        // Validate the personType parameter.
        if (personType == null || personType.trim().isEmpty() ||
                (!personType.trim().equalsIgnoreCase("actor") && !personType.trim().equalsIgnoreCase("crew"))) {
            throw new WrongParamException("Invalid personType: " + personType);
        }
        // Validate the personName parameter.
        if (personName == null || personName.trim().isEmpty()) {
            throw new WrongParamException("Invalid personName: " + personName);
        }

        int page = offset / PAGINATION_LIMIT;
        Pageable pageable = PageRequest.of(page, PAGINATION_LIMIT);

        List<Movie> movies;
        if (personType.trim().equalsIgnoreCase("actor")) {
            movies = movieRepository.findMoviesByActorsActorNameEqualsIgnoreCaseOrderByIdAsc(personName, pageable);
        } else { // personType is "crew"
            movies = movieRepository.findMoviesByCrewCrewNameEqualsIgnoreCaseOrderByIdAsc(personName, pageable);
        }
        
        if (movies.isEmpty()) {
            throw new MovieNotFoundException("No movies found for " + personType + " with name: " + personName);
        }

        return movies.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Private method to convert a Movie entity to a MovieSummaryDTO.
     *
     * @param movie the Movie entity.
     * @return a DTO containing summary information of the movie.
     */
    private MovieSummaryDTO convertToSummaryDTO(Movie movie) {
        String posterLink = (movie.getPoster() != null) ? movie.getPoster().getPosterLink() : "";
        return new MovieSummaryDTO(movie.getId(), movie.getTitle(), posterLink);
    }

    /**
     * Private method to convert an Optional<Movie> to a MovieDetailedDTO.
     *
     * @param movieOptional the Optional containing the Movie entity.
     * @return a DTO containing detailed information of the movie, or an empty Optional if not found.
     */
    private Optional<MovieDetailedDTO> convertToDetailedDTO(Optional<Movie> movieOptional) {
        if (movieOptional.isEmpty()) {
            return Optional.empty();
        }
        Movie movie = movieOptional.get();
        MovieDetailedDTO dto = new MovieDetailedDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setYear(movie.getYear());
        dto.setTagline(movie.getTagline());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setRating(movie.getRating());
        dto.setLanguages(languageService.convertToLanguageDTO(movie.getLanguages()));
        dto.setProductionCountries(movie.getProductionCountries().stream()
                .map(ProductionCountry::getCountry)
                .collect(Collectors.toList()));
        dto.setPoster((movie.getPoster() != null) ? movie.getPoster().getPosterLink() : "");
        dto.setStudios(movie.getStudios().stream().map(Studio::getStudio).collect(Collectors.toList()));
        dto.setThemes(movie.getThemes().stream().map(Theme::getTheme).collect(Collectors.toList()));
        dto.setGenres(movie.getGenres().stream().map(Genre::getGenre).collect(Collectors.toList()));
        dto.setCrew(crewService.convertToCrewDTO(movie.getCrew()));
        dto.setActors(actorService.convertToActorDTO(movie.getActors()));
        return Optional.of(dto);
    }
}