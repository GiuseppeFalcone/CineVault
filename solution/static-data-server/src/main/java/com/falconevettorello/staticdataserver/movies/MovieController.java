package com.falconevettorello.staticdataserver.movies;

import com.falconevettorello.staticdataserver.exceptions.*;
import com.falconevettorello.staticdataserver.movies.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for handling movie-related requests.
 * Each endpoint returns a ResponseEntity to provide full control over HTTP status and headers.
 * If an error occurs, exception handlers will send the error response to the client and print the error to the terminal.
 */
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(final MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Retrieves a movie by its ID.
     *
     * @param id the movie's ID.
     * @return ResponseEntity containing the movie.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieDetailedDTO> getMovie(@PathVariable Long id) {
        MovieDetailedDTO movie = movieService.getMovieById(id);
        if (movie == null) {
            throw new MovieNotFoundException("Movie with ID " + id + " not found.");
        }
        return ResponseEntity.ok(movie);
    }

    /**
     * Retrieves movies related to a given movie based on its genres.
     *
     * @param movieId the ID of the movie to find related movies for.
     * @return ResponseEntity containing a list of movie summaries.
     */
    @GetMapping("/get-related")
    public ResponseEntity<List<MovieSummaryDTO>> getRelated(@RequestParam(required = true) Long movieId) {
        List<MovieSummaryDTO> relatedMovies = movieService.getRelatedMovies(movieId);
        return ResponseEntity.ok(relatedMovies);
    }

    /**
     * Retrieves a list of movies grouped by genres
     *
     * @return ResponseEntity containing a list of movie categories based on genres.
     */
    @GetMapping("/random-genre-based")
    public ResponseEntity<List<MoviesCategoryBasedDTO>> getRandomMoviesGenreBased() {
        List<MoviesCategoryBasedDTO> result = movieService.getRandomMoviesGenreBased();
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves the latest category of movies (recent movies).
     *
     * @return ResponseEntity containing the latest movies category.
     */
    @GetMapping("/get-latest")
    public ResponseEntity<MoviesCategoryBasedDTO> getLatestMovies() {
        MoviesCategoryBasedDTO latestMovies = movieService.getLatestMovies();
        return ResponseEntity.ok(latestMovies);
    }

    /**
     * Retrieves a paginated list of movies (20 movies per page).
     *
     * @param offset pagination offset.
     * @return ResponseEntity containing a list of movie summaries.
     */
    @GetMapping("/get-20-movies")
    public ResponseEntity<List<MovieSummaryDTO>> getPaginatedMovies(
            @RequestParam(defaultValue = "0") int offset) {
        List<MovieSummaryDTO> movies = movieService.findPaginatedMovies(offset);
        return ResponseEntity.ok(movies);
    }

    /**
     * Retrieves movies filtered by their titles.
     *
     * @param movieTitles list of movie titles.
     * @return ResponseEntity containing a list of movie summaries.
     */
    @PostMapping("/get-by-titles")
    public ResponseEntity<List<MovieSummaryDTO>> getMoviesByTitles(@RequestBody(required = true) List<String> movieTitles) {
        List<MovieSummaryDTO> movies = movieService.getMoviesByTitles(movieTitles);
        return ResponseEntity.ok(movies);
    }

    /**
     * Retrieves movies filtered by various parameters.
     *
     * @param filter filter criteria.
     * @return ResponseEntity containing a list of movie summaries.
     */
    @PostMapping("/get-filtered-movies")
    public ResponseEntity<List<MovieSummaryDTO>> getFilteredMovies(@RequestBody(required = true) MovieFilterRequest filter) {
        List<MovieSummaryDTO> movies = movieService.getFilteredMovies(filter);
        return ResponseEntity.ok(movies);
    }

    /**
     * Retrieves the unique filters available (genres, languages, countries, release years).
     *
     * @return ResponseEntity containing a map of filter names to lists of values.
     */
    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<String>>> getUniqueFilters() {
        Map<String, List<String>> filters = movieService.getUniqueFilters();
        return ResponseEntity.ok(filters);
    }

    /**
     * Searches movies based on a specific field.
     *
     * @param field  the field to search by.
     * @param value  the value to search for.
     * @param offset pagination offset.
     * @return ResponseEntity containing a list of movie summaries matching the search criteria.
     */
    @GetMapping("/search")
    public ResponseEntity<List<MovieSummaryDTO>> searchMoviesSingle(
            @RequestParam(required = true) String field,
            @RequestParam(required = true) String value,
            @RequestParam(defaultValue = "0") int offset) {
        List<MovieSummaryDTO> movies = movieService.search(field, value, offset);
        return ResponseEntity.ok(movies);
    }

    /**
     * Retrieves movies associated with an actor or crew member based on the person's name.
     *
     * @param personType the type of person ("actor" or "crew").
     * @param personName the name of the person.
     * @param offset     pagination offset.
     * @return ResponseEntity containing a list of movie summaries.
     */
    @GetMapping("/by-name")
    public ResponseEntity<List<MovieSummaryDTO>> getMoviesByActorName(
            @RequestParam(required = true) String personType,
            @RequestParam(required = true) String personName,
            @RequestParam(defaultValue = "0") int offset) {
        List<MovieSummaryDTO> movies = movieService.getMoviesByName(personType, personName, offset);
        return ResponseEntity.ok(movies);
    }

    // Exception handling for custom responses

    /**
     * Handles MovieNotFoundException, prints the error to the terminal, and returns a 404 status with an error message.
     *
     * @param exception the exception thrown.
     * @return ResponseEntity containing the error message.
     */
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<String> handleMovieNotFound(MovieNotFoundException exception) {
        // Print the error to the terminal
        System.err.println("MovieNotFoundException: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    /**
     * Handles WrongParamException, prints the error to the terminal, and returns a 400 status with an error message.
     *
     * @param exception the exception thrown.
     * @return ResponseEntity containing the error message.
     */
    @ExceptionHandler(WrongParamException.class)
    public ResponseEntity<String> handleWrongParam(WrongParamException exception) {
        // Print the error to the terminal
        System.err.println("WrongParamException: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
