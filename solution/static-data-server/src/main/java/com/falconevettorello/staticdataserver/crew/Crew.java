package com.falconevettorello.staticdataserver.crew;

import com.falconevettorello.staticdataserver.movies.Movie;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@Table(name = "crew")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Crew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String crewRole;

    private String crewName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    // Constructor
    public Crew(){}

    public Crew(String crewRole, String CrewName, Movie movie) {
        this.crewRole = crewRole;
        this.crewName = CrewName;
        this.movie = movie;
    }

    // Methods getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrewRole() {
        return crewRole;
    }

    public void setCrewRole(String role) {
        this.crewRole = role;
    }

    public String getCrewName() {
        return crewName;
    }

    public void setCrewName(String name) {
        this.crewName = name;
    }

    public Movie getMovie() {return movie;}

    public void setMovie(Movie movie) {this.movie = movie;}

}
