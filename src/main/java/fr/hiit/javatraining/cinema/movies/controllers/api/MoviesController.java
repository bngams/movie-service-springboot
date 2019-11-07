package fr.hiit.javatraining.cinema.movies.controllers.api;

import fr.hiit.javatraining.cinema.movies.models.TMDBMovie;
import fr.hiit.javatraining.cinema.movies.models.TMDBResult;
import fr.hiit.javatraining.cinema.movies.services.TMDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/movie")
public class MoviesController {

    @Autowired
    private TMDBService tmdbService;

    /**
     * Fetch movies from TMDB API
     * Execute two request in parallel
     * INFO: for better exception management in stream
     * https://www.oreilly.com/ideas/handling-checked-exceptions-in-java-streams
     * @return
     * @throws InterruptedException
     */
    @GetMapping("")
    public ResponseEntity<List<TMDBMovie>> fetchMoviesWithCallableAndExecutors() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();

        Callable<TMDBResult> requestUpcoming = () -> {
            return restTemplate.getForObject(this.tmdbService.formatUrl("/movie/upcoming"), TMDBResult.class);
        };

        Callable<TMDBResult> requestNowPlaying = () -> {
            return restTemplate.getForObject(this.tmdbService.formatUrl("/movie/now_playing"), TMDBResult.class);
        };

        ExecutorService executorService = Executors.newWorkStealingPool();
        List<TMDBMovie> movies = executorService.invokeAll(List.of(requestUpcoming, requestNowPlaying))
                .stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .map(TMDBResult::getResults)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/service")
    public ResponseEntity<List<TMDBMovie>> fetchMoviesWithServiceAndComparableFuture() throws ExecutionException, InterruptedException {
        CompletableFuture<List<TMDBMovie>> moviesUpcoming = tmdbService.getMovies("upcoming");
        CompletableFuture<List<TMDBMovie>> moviesNowPlaying = tmdbService.getMovies("now_playing");
        List<TMDBMovie> movies = Stream.concat(moviesUpcoming.get().stream(), moviesNowPlaying.get().stream())
                    .collect(Collectors.toList());
        // or
        // List<TMDBMovie> movies = Stream.of(moviesUpcoming.get(), moviesNowPlaying.get()).flatMap(List::stream).collect(Collectors.toList());
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}
