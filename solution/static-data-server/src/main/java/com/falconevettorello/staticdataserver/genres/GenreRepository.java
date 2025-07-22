package com.falconevettorello.staticdataserver.genres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Query(value = "SELECT DISTINCT genre FROM genres", nativeQuery = true)
    List<String> findDistinctGenres();

}
