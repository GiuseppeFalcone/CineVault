package com.falconevettorello.staticdataserver.releases;

import com.falconevettorello.staticdataserver.movies.Movie;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "releases")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Release {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String releaseCountry;
    private LocalDate releaseDate;
    private String releaseType;
    private String countryContentRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    public Release() {
    }

    public Release(Long id, String releaseCountry, LocalDate releaseDate, String releaseType, String countryContentRating, Movie movie) {
        this.id = id;
        this.releaseCountry = releaseCountry;
        this.releaseDate = releaseDate;
        this.releaseType = releaseType;
        this.countryContentRating = countryContentRating;
        this.movie = movie;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReleaseCountry() {
        return releaseCountry;
    }

    public void setReleaseCountry(String releaseCountry) {
        this.releaseCountry = releaseCountry;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    public String getCountryContentRating() {
        return countryContentRating;
    }

    public void setCountryContentRating(String countryContentRating) {
        this.countryContentRating = countryContentRating;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
