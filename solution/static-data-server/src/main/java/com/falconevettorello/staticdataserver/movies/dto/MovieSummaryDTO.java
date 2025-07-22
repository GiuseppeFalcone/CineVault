package com.falconevettorello.staticdataserver.movies.dto;

public class MovieSummaryDTO {
    private Long id;
    private String title;
    private String posterLink;

    public MovieSummaryDTO() {}
    public MovieSummaryDTO(Long id, String title, String posterLink) {
        this.id = id;
        this.title = title;
        this.posterLink = posterLink;
    }

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

    public String getPosterLink() {
        return posterLink;
    }

    public void setPosterLink(String posterLink) {
        this.posterLink = posterLink;
    }
}
