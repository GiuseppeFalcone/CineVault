package com.falconevettorello.staticdataserver.config;

import com.falconevettorello.staticdataserver.actors.*;
import com.falconevettorello.staticdataserver.crew.*;
import com.falconevettorello.staticdataserver.genres.*;
import com.falconevettorello.staticdataserver.movies.*;
import com.falconevettorello.staticdataserver.languages.*;
import com.falconevettorello.staticdataserver.posters.*;
import com.falconevettorello.staticdataserver.releases.*;
import com.falconevettorello.staticdataserver.productionCountries.*;
import com.falconevettorello.staticdataserver.studios.*;
import com.falconevettorello.staticdataserver.themes.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private ProductionCountryRepository productionCountryRepository;
    @Autowired
    private PosterRepository posterRepository;
    @Autowired
    private StudioRepository studioRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private CrewRepository crewRepository;
    @Autowired
    private ActorRepository actorRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting database initialization...");

        importMovies();
        importLanguages();
        importReleases();
        importProductionCountries();
        importPosters();
        importStudios();
        importThemes();
        importGenres();
        importCrew();
        importActors();

        System.out.println("Finishing database initialization...");
    }

    private void importMovies() {
        File movieFile = new File("../data/movies.csv");
        if (!movieFile.exists()) {
            throw new RuntimeException("Movies CSV file not found: " + movieFile.getAbsolutePath());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(movieFile)); CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withQuote('"').withTrim())) {
            List<CSVRecord> records = csvParser.getRecords();
            int csvEntries = records.size();
            if (csvEntries == movieRepository.count()) {
                System.out.println("Movies already initialized");
                return;
            } else {
                movieRepository.deleteAll();
            }

            System.out.println("Importing movies...");
            List<Movie> movieList = new ArrayList<>();
            for (CSVRecord record : records) {
                Movie movie = new Movie();
                movie.setId(Long.parseLong(record.get("id")));
                movie.setTitle(CSVUtil.parseString(record.get("name")));
                movie.setYear(CSVUtil.parseYear(record.get("date")));
                movie.setTagline(CSVUtil.parseString(record.get("tagline")));
                movie.setDescription(CSVUtil.parseString(record.get("description")));
                movie.setDuration(CSVUtil.parseInt(record.get("minute")));
                movie.setRating(CSVUtil.parseDouble(record.get("rating")));
                movieList.add(movie);

                if (movieList.size() == 1000) {
                    movieRepository.saveAll(movieList);
                    movieList.clear();
                }
            }
            movieRepository.saveAll(movieList);
            if (csvEntries != movieRepository.count()) {
                throw new RuntimeException("Movie count mismatch");
            }
            System.out.println("Finished importing movies.");
        } catch (IOException e) {
            throw new RuntimeException("Error importing movies CSV", e);
        }
    }

    private void importLanguages() {
        File languageFile = new File("../data/languages.csv");
        if (!languageFile.exists()) {
            throw new RuntimeException("Languages CSV file not found: " + languageFile.getAbsolutePath());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(languageFile)); CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {
            List<CSVRecord> records = csvParser.getRecords();
            int csvEntries = records.size();

            if (csvEntries == languageRepository.count()) {
                System.out.println("Languages already initialized");
                return;
            } else {
                languageRepository.deleteAll();
            }

            System.out.println("Importing languages...");
            Movie movie = null;
            Long oldMovieId = null;
            List<Language> languageList = new ArrayList<>();
            for (CSVRecord record : records) {
                Long movieId = Long.parseLong(record.get("id"));
                String type = CSVUtil.parseString(record.get("type"));
                String language = CSVUtil.parseString(record.get("language"));

                // Retrieve the corresponding Movie entity
                if (movie == null || !oldMovieId.equals(movieId)) {
                    movie = movieRepository.findById(movieId)
                            .orElseThrow(() -> new RuntimeException("Movie not found for id: " + movieId));
                    oldMovieId = movieId;
                }

                // Create and save the MovieLanguage record
                Language movieLanguage = new Language();
                movieLanguage.setType(type);
                movieLanguage.setLanguage(language);
                movieLanguage.setMovie(movie);

                languageList.add(movieLanguage);
                if (languageList.size() == 1000) {
                    languageRepository.saveAll(languageList);
                    languageList.clear();
                }
            }
            languageRepository.saveAll(languageList);
            if (csvEntries != languageRepository.count()) {
                throw new RuntimeException("Language count mismatch");
            }
            System.out.println("Finished importing languages.");
        } catch (IOException e) {
            throw new RuntimeException("Error importing languages CSV", e);
        }
    }

    private void importReleases() {
        File releaseFile = new File("../data/releases.csv");
        if (!releaseFile.exists()) {
            throw new RuntimeException("Releases CSV file not found: " + releaseFile.getAbsolutePath());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(releaseFile));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withTrim())) {
            List<CSVRecord> records = csvParser.getRecords();
            int csvEntries = records.size();

            if (csvEntries == releaseRepository.count()) {
                System.out.println("Releases already initialized");
                return;
            } else {
                releaseRepository.deleteAll();
            }

            System.out.println("Importing releases...");
            Movie movie = null;
            Long oldMovieId = null;
            List<Release> releaseList = new ArrayList<>();
            for (CSVRecord record : records) {
                Long movieId = Long.parseLong(record.get("id"));
                String releaseCountry = CSVUtil.parseString(record.get("country"));
                LocalDate releaseDate = CSVUtil.parseDate(record.get("date"));
                String releaseType = CSVUtil.parseString(record.get("type"));
                String releaseCountryContentRating = CSVUtil.parseString(record.get("rating"));

                // Retrieve the corresponding Movie entity
                if (movie == null || !oldMovieId.equals(movieId)) {
                    movie = movieRepository.findById(movieId)
                            .orElseThrow(() -> new RuntimeException("Movie not found for id: " + movieId));
                    oldMovieId = movieId;
                }

                Release movieRelease = new Release();
                movieRelease.setReleaseCountry(releaseCountry);
                movieRelease.setReleaseDate(releaseDate);
                movieRelease.setReleaseType(releaseType);
                movieRelease.setCountryContentRating(releaseCountryContentRating);
                movieRelease.setMovie(movie);

                releaseList.add(movieRelease);
                if (releaseList.size() == 10000) {
                    releaseRepository.saveAll(releaseList);
                    releaseList.clear();
                }
            }
            releaseRepository.saveAll(releaseList);
            if (csvEntries != releaseRepository.count()) {
                throw new RuntimeException("Release count mismatch");
            }
            System.out.println("Finished importing releases.");
        } catch (IOException e) {
            throw new RuntimeException("Error importing releases CSV", e);
        }
    }

    private void importProductionCountries() {
        // Adjust the file path to your CSV file as needed.
        File productionCountryFile = new File("../data/countries.csv");
        if (!productionCountryFile.exists()) {
            throw new RuntimeException("Production countries CSV file not found: " + productionCountryFile.getAbsolutePath());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(productionCountryFile));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withTrim())) {
            List<CSVRecord> records = csvParser.getRecords();
            int csvEntries = records.size();

            if (csvEntries == productionCountryRepository.count()) {
                System.out.println("Production countries already initialized");
                return;
            } else
                productionCountryRepository.deleteAll();

            System.out.println("Importing production countries...");
            Movie movie = null;
            Long oldMovieId = null;
            List<ProductionCountry> prodCountryList = new ArrayList<>();
            for (CSVRecord record : records) {
                Long movieId = Long.parseLong(record.get("id"));
                String country = CSVUtil.parseString(record.get("country"));
                // Retrieve the corresponding Movie entity
                if (movie == null || !oldMovieId.equals(movieId)) {
                    movie = movieRepository.findById(movieId)
                            .orElseThrow(() -> new RuntimeException("Movie not found for id: " + movieId));
                    oldMovieId = movieId;
                }
                ProductionCountry productionCountry = new ProductionCountry();
                productionCountry.setCountry(country);
                productionCountry.setMovie(movie);

                prodCountryList.add(productionCountry);
                if (prodCountryList.size() == 10000) {
                    productionCountryRepository.saveAll(prodCountryList);
                    prodCountryList.clear();
                }
            }
            productionCountryRepository.saveAll(prodCountryList);
            if (csvEntries != productionCountryRepository.count()) {
                throw new RuntimeException("Production countries count mismatch");
            }
            System.out.println("Finished importing production countries.");
        } catch (IOException e) {
            throw new RuntimeException("Error importing production countries CSV", e);
        }
    }

    protected void importPosters() {
        File posterFile = new File("../data/posters.csv");
        if (!posterFile.exists()) {
            throw new RuntimeException("Posters CSV file not found: " + posterFile.getAbsolutePath());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(posterFile));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withTrim())) {

            List<CSVRecord> records = csvParser.getRecords();
            int csvEntries = records.size();

            if (csvEntries == posterRepository.count()) {
                System.out.println("Posters already initialized");
                return;
            } else
                posterRepository.deleteAll();

            System.out.println("Importing posters...");
            Movie movie = null;
            Long oldMovieId = null;
            List<Poster> posterList = new ArrayList<>();
            for (CSVRecord record : records) {
                Long movieId = Long.parseLong(record.get("id"));
                String posterLink = CSVUtil.parseString(record.get("link"));

                // Retrieve the corresponding Movie entity
                if (movie == null || !oldMovieId.equals(movieId)) {
                    movie = movieRepository.findById(movieId)
                            .orElseThrow(() -> new RuntimeException("Movie not found for id: " + movieId));
                    oldMovieId = movieId;
                }

                Poster poster = new Poster();
                poster.setPosterLink(posterLink);
                poster.setMovie(movie);

                posterList.add(poster);
                if (posterList.size() == 10000) {
                    posterRepository.saveAll(posterList);
                    posterList.clear();
                }
            }
            posterRepository.saveAll(posterList);
            if (csvEntries != posterRepository.count()) {
                throw new RuntimeException("Posters count mismatch");
            }
            System.out.println("Finished importing posters.");
        } catch (IOException e) {
            throw new RuntimeException("Error importing posters CSV", e);
        }
    }

    private void importStudios() {
        File studioFile = new File("../data/studios.csv");
        if (!studioFile.exists()) {
            throw new RuntimeException("Studio CSV file not found: " + studioFile.getAbsolutePath());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(studioFile));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withTrim())) {

            List<CSVRecord> records = csvParser.getRecords();
            int csvEntries = records.size();

            if (csvEntries == studioRepository.count()) {
                System.out.println("Studio already initialized");
                return;
            } else
                studioRepository.deleteAll();

            System.out.println("Importing studios...");
            List<Studio> studioList = new ArrayList<>();
            Movie movie = null;
            Long oldMovieId = null;
            for (CSVRecord record : records) {
                Long movieId = Long.parseLong(record.get("id"));
                String studioName = CSVUtil.parseString(record.get("studio"));

                if (movie == null || !oldMovieId.equals(movieId)) {
                    movie = movieRepository.findById(movieId)
                            .orElseThrow(() -> new RuntimeException("Movie not found for id: " + movieId));
                    oldMovieId = movieId;
                }

                Studio studio = new Studio();
                studio.setStudio(studioName);
                studio.setMovie(movie);

                studioList.add(studio);

                if(studioList.size() == 10000) {
                    studioRepository.saveAll(studioList);
                    studioList.clear();
                }
            }

            studioRepository.saveAll(studioList);

            if (csvEntries != studioRepository.count()) {
                throw new RuntimeException("Studio count mismatch");
            }
            System.out.println("Finished importing studios.");
        } catch (IOException e) {
            throw new RuntimeException("Error importing studios CSV", e);
        }
    }

    private void importThemes() {
        File themeFile = new File("../data/themes.csv");
        if (!themeFile.exists()) {
            throw new RuntimeException("Theme CSV file not found: " + themeFile.getAbsolutePath());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(themeFile));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withTrim())){

            List<CSVRecord> records = csvParser.getRecords();
            int csvEntries = records.size();

            if (csvEntries== themeRepository.count()) {
                System.out.println("Theme already initialized");
                return;
            } else
                themeRepository.deleteAll();

            System.out.println("Importing themes...");
            List<Theme> themeList = new ArrayList<>();
            Movie movie = null;
            Long oldMovieId = null;
            for (CSVRecord record : records) {
                Long movieId = Long.parseLong(record.get("id"));
                String themeName = CSVUtil.parseString(record.get("theme"));

                if (movie == null || !oldMovieId.equals(movieId)) {
                    movie = movieRepository.findById(movieId)
                            .orElseThrow(() -> new RuntimeException("Movie not found for id: " + movieId));
                    oldMovieId = movieId;
                }

                Theme theme = new Theme();
                theme.setTheme(themeName);
                theme.setMovie(movie);

                themeList.add(theme);

                if(themeList.size() == 10000) {
                    themeRepository.saveAll(themeList);
                    themeList.clear();
                }
            }

            themeRepository.saveAll(themeList);

            if(csvEntries != themeRepository.count()) {
                throw new RuntimeException("Theme count mismatch");
            }
            System.out.println("Finished importing themes.");
        } catch (IOException e) {
            throw new RuntimeException("Error importing themes CSV", e);
        }
    }

    private void importGenres() {
        File genreFile = new File("../data/genres.csv");
        if (!genreFile.exists()) {
            throw new RuntimeException("Genre CSV file not found: " + genreFile.getAbsolutePath());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(genreFile));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withTrim())){

            List<CSVRecord> records = csvParser.getRecords();
            int csvEntries = records.size();

            if (csvEntries == genreRepository.count()) {
                System.out.println("Genre already initialized");
                return;
            } else
                genreRepository.deleteAll();

            System.out.println("Importing genres...");
            List<Genre> genreList = new ArrayList<>();
            Movie movie = null;
            Long oldMovieId = null;
            for (CSVRecord record : records) {
                Long movieId = Long.parseLong(record.get("id"));
                String genreName = CSVUtil.parseString(record.get("genre"));
                if (movie == null || !oldMovieId.equals(movieId)) {
                    movie = movieRepository.findById(movieId)
                            .orElseThrow(() -> new RuntimeException("Movie not found for id: " + movieId));
                    oldMovieId = movieId;
                }

                Genre genre = new Genre();
                genre.setGenre(genreName);
                genre.setMovie(movie);

                genreList.add(genre);

                if(genreList.size() == 10000) {
                    genreRepository.saveAll(genreList);
                    genreList.clear();
                }
            }

            genreRepository.saveAll(genreList);

            if(csvEntries != genreRepository.count()) {
                throw new RuntimeException("Genre count mismatch");
            }
            System.out.println("Finished importing genres.");
        }  catch (IOException e) {
            throw new RuntimeException("Error importing genres CSV", e);
        }
    }

    private void importCrew() {
        File crewFile = new File("../data/crew.csv");
        if (!crewFile.exists()) {
            throw new RuntimeException("Crew CSV file not found: " + crewFile.getAbsolutePath());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(crewFile));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withTrim())){
            List<CSVRecord> records = csvParser.getRecords();
            int csvEntries = records.size();

            if (csvEntries == crewRepository.count()) {
                System.out.println("Crew already initialized");
                return;
            } else
                crewRepository.deleteAll();

            System.out.println("Importing crew...");
            List<Crew> crewList = new ArrayList<>();
            Movie movie = null;
            Long oldMovieId = null;
            for (CSVRecord record : records) {
                Long movieId = Long.parseLong(record.get("id"));
                String crewRole = CSVUtil.parseString(record.get("role"));
                String crewName = CSVUtil.parseString(record.get("name"));

                if (movie == null || !oldMovieId.equals(movieId)) {
                    movie = movieRepository.findById(movieId)
                            .orElseThrow(() -> new RuntimeException("Movie not found for id: " + movieId));
                    oldMovieId = movieId;
                }

                Crew crew = new Crew();
                crew.setCrewRole(crewRole);
                crew.setCrewName(crewName);
                crew.setMovie(movie);

                crewList.add(crew);

                if(crewList.size() == 10000) {
                    crewRepository.saveAll(crewList);
                    crewList.clear();
                }
            }

            crewRepository.saveAll(crewList);

            if(csvEntries != crewRepository.count()) {
                throw new RuntimeException("Crew count mismatch");
            }
            System.out.println("Finished importing crews.");
        } catch (IOException e) {
            throw new RuntimeException("Error importing crews CSV", e);
        }
    }

    private void importActors() {
        File actorsFile = new File("../data/actors.csv");
        if (!actorsFile.exists()) {
            throw new RuntimeException("Actor CSV file not found: " + actorsFile.getAbsolutePath());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(actorsFile));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withTrim())){
            List<CSVRecord> records = csvParser.getRecords();
            int csvEntries = records.size();

            if (csvEntries == actorRepository.count()) {
                System.out.println("Actor already initialized");
                return;
            } else
                actorRepository.deleteAll();

            System.out.println("Importing actors...");
            List<Actor> actorList = new ArrayList<>();
            Movie movie = null;
            Long oldMovieId = null;
            for (CSVRecord record : records) {
                Long movieId = Long.parseLong(record.get("id"));
                String actorName = CSVUtil.parseString(record.get("name"));
                String actorRole = CSVUtil.parseString(record.get("role"));

                if (movie == null || !oldMovieId.equals(movieId)) {
                    movie = movieRepository.findById(movieId)
                            .orElseThrow(() -> new RuntimeException("Movie not found for id: " + movieId));
                    oldMovieId = movieId;
                }

                Actor actor = new Actor();
                actor.setName(actorName);
                actor.setRole(actorRole);
                actor.setMovie(movie);

                actorList.add(actor);

                if(actorList.size() == 10000) {
                    actorRepository.saveAll(actorList);
                    actorList.clear();
                }
            }

            actorRepository.saveAll(actorList);

            if(csvEntries != actorRepository.count()) {
                throw new RuntimeException("Actor count mismatch");
            }
            System.out.println("Finished importing actors.");
        } catch (IOException e) {
            throw new RuntimeException("Error importing actors CSV", e);
        }
    }
}

class CSVUtil {
    public static String parseString(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return input.trim();
    }

    public static Year parseYear(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return Year.of(Integer.parseInt(input));
    }

    public static Integer parseInt(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return Integer.parseInt(input);
    }

    public static Double parseDouble(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return Double.parseDouble(input);
    }

    public static LocalDate parseDate(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return LocalDate.parse(input);
    }
}