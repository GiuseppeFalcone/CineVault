package com.falconevettorello.staticdataserver.actors;

import com.falconevettorello.staticdataserver.movies.Movie;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@Table(name = "actors")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actorName;

    private String actorRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    // Constructor
    public Actor() {}

    public Actor(String name, String role, Movie movie) {
        this.actorName = name;
        this.actorRole = role;
        this.movie = movie;
    }

    // Methods getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return actorName;
    }

    public void setName(String name) {
        this.actorName = name;
    }

    public String getRole() {
        return actorRole;
    }

    public void setRole(String role) {
        this.actorRole = role;
    }

    public Movie getMovie() {return movie;}

    public void setMovie(Movie movie) {this.movie = movie;}
}
