package com.falconevettorello.staticdataserver.movies.dto;

import com.falconevettorello.staticdataserver.actors.ActorDTO;
import com.falconevettorello.staticdataserver.crew.CrewDTO;
import com.falconevettorello.staticdataserver.languages.LanguageDTO;
import java.time.Year;
import java.util.List;

public class MovieDetailedDTO {
    private Long id;
    private String title;
    private Year year;
    private String tagline;
    private String description;
    private Integer duration;
    private Double rating;
    private List<LanguageDTO> languages;
    private List<String> productionCountries;
    private String poster;
    private List<String> studios;
    private List<String> themes;
    private List<String> genres;
    private List<CrewDTO> crew;
    private List<ActorDTO> actors;

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

    public List<LanguageDTO> getLanguages() {
        return languages;
    }
    public void setLanguages(List<LanguageDTO> languages) {
        this.languages = languages;
    }

    public List<String> getProductionCountries() {
        return productionCountries;
    }
    public void setProductionCountries(List<String> productionCountries) {
        this.productionCountries = productionCountries;
    }
    
    public String getPoster() {
        return poster;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }
    
    public List<String> getStudios() {
        return studios;
    }
    public void setStudios(List<String> studios) {
        this.studios = studios;
    }

    public List<String> getThemes() {
        return themes;
    }
    public void setThemes(List<String> themes) {
        this.themes = themes;
    }

    public List<String> getGenres() {
        return genres;
    }
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<CrewDTO> getCrew() {
        return crew;
    }
    public void setCrew(List<CrewDTO> crew) {
        this.crew = crew;
    }

    public List<ActorDTO> getActors() {
        return actors;
    }
    public void setActors(List<ActorDTO> actors) {
        this.actors = actors;
    }

}
