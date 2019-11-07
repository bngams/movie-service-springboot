package fr.hiit.javatraining.cinema.movies.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMDBResult {
    private Integer page;
    private List<TMDBMovie> results;
}
