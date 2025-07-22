package com.falconevettorello.staticdataserver.movies;

import com.falconevettorello.staticdataserver.actors.Actor;
import com.falconevettorello.staticdataserver.crew.Crew;
import com.falconevettorello.staticdataserver.genres.Genre;
import com.falconevettorello.staticdataserver.languages.Language;
import com.falconevettorello.staticdataserver.posters.Poster;
import com.falconevettorello.staticdataserver.productionCountries.ProductionCountry;
import com.falconevettorello.staticdataserver.releases.Release;

import com.falconevettorello.staticdataserver.studios.Studio;
import com.falconevettorello.staticdataserver.themes.Theme;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.Year;
import java.util.*;

@Entity
@Table(name = "movies")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Movie {

    @Id
    private Long id;

    private String title;

    private Year year;

    private String tagline;

    @Column(length = 2000)
    private String description;

    private Integer duration;

    private Double rating;

    // One movie can have many language records.
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Language> languages;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Release> releases;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductionCountry> productionCountries;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Poster poster;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Studio> studios;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Theme> themes;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Genre> genres;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Crew> crew;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Actor> actors;

    // Default constructor
    public Movie() {
        languages = new ArrayList<>();
        releases = new ArrayList<>();
        productionCountries = new ArrayList<>();
        studios = new ArrayList<>();
        themes = new ArrayList<>();
        crew = new ArrayList<>();
        actors = new ArrayList<>();
    }

    // All-args constructor
    public Movie(Long id, String title, Year year, String tagline, String description, Integer duration, Double rating) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.tagline = tagline;
        this.description = description;
        this.duration = duration;
        this.rating = rating;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Year getYear() {
        return year;
    }
    public void setYear(Year year) {
        this.year = year;
    }

    public String getTagline() {
        return tagline;
    }
    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Double getRating() {
        return rating;
    }
    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<Language> getLanguages() {
        return languages;
    }
    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<Release> getReleases() {
        return releases;
    }
    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }

    public List<ProductionCountry> getProductionCountries() {
        return productionCountries;
    }
    public void setProductionCountries(List<ProductionCountry> productionCountries) {
        this.productionCountries = productionCountries;
    }

    public Poster getPoster() {
        return poster;
    }
    public void setPoster(Poster poster) {
        this.poster = poster;
    }

    public List<Genre> getGenres() {return genres;}
    public void setGenres(List<Genre> genres) {this.genres = genres;}

    public List<Actor> getActors() {return actors;}
    public void setActors(List<Actor> actors) {this.actors = actors;}

    public List<Theme> getThemes() {return themes;}
    public void setThemes(List<Theme> themes) {this.themes = themes;}

    public List<Crew> getCrew() {return crew;}
    public void setCrew(List<Crew> crews) {this.crew = crews;}

    // Convenience methods to maintain bidirectional relationships
    
    public void addLanguage(Language language) {
        languages.add(language);
        language.setMovie(this);
    }

    public void addRelease(Release release) {
        releases.add(release);
        release.setMovie(this);
    }

    public void addProductionCountry(ProductionCountry productionCountry) {
        productionCountries.add(productionCountry);
        productionCountry.setMovie(this);
    }
    

    public void addStudio(Studio studio) {
        studios.add(studio);
        studio.setMovie(this);
    }

    public List<Studio> getStudios() {
        return studios;
    }

    public void setStudios(List<Studio> studios) {
        this.studios = studios;
    }

    public void addTheme(Theme theme) {
        themes.add(theme);
        theme.setMovie(this);
    }

    public void addGenres(Genre genre) {
        genres.add(genre);
        genre.setMovie(this);
    }

    public void addActors(Actor actor) {
        actors.add(actor);
        actor.setMovie(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return id != null && id.equals(movie.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
