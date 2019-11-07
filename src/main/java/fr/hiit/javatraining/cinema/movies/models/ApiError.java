package fr.hiit.javatraining.cinema.movies.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ApiError {
    private List<String> errors;
}