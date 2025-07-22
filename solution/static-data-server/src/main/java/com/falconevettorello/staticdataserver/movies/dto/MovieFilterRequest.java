package com.falconevettorello.staticdataserver.movies.dto;

import java.util.List;

public class MovieFilterRequest {
    private String genre;
    private String language;
    private String country;
    private Integer releaseYear;
    private List<String> movieTitles;
    private Integer offset;

    // Getters e Setters

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public List<String> getMovieTitles() {
        return movieTitles;
    }

    public void setMovieTitles(List<String> movieTitles) {
        this.movieTitles = movieTitles;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}