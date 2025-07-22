package com.falconevettorello.staticdataserver.movies.dto;

import java.util.List;

public class MoviesCategoryBasedDTO {
    private String category;

    private List<MovieSummaryDTO> movieSummaryDTOs;

    public MoviesCategoryBasedDTO(String category, List<MovieSummaryDTO> movieSummaryDTOs) {
        this.category = category;
        this.movieSummaryDTOs = movieSummaryDTOs;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<MovieSummaryDTO> getMovieSummaryDTOs() {
        return movieSummaryDTOs;
    }

    public void setMovieSummaryDTOs(List<MovieSummaryDTO> movieSummaryDTOs) {
        this.movieSummaryDTOs = movieSummaryDTOs;
    }
}
