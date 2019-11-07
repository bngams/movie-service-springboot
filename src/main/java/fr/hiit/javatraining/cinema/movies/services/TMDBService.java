package fr.hiit.javatraining.cinema.movies.services;

import fr.hiit.javatraining.cinema.movies.config.TMDBConfigProperties;
import fr.hiit.javatraining.cinema.movies.models.TMDBMovie;
import fr.hiit.javatraining.cinema.movies.models.TMDBResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class TMDBService {
    @Value("${services.tmdb.apiBaseUrl}")
    private String apiBaseUrl;

    @Value("${services.tmdb.apiKey}")
    private String apiKey;

    @Autowired
    private TMDBConfigProperties env;

    RestTemplate restTemplate = new RestTemplate();

    @Async
    public CompletableFuture<List<TMDBMovie>> getMovies(String endpoint) {
        endpoint = Optional.ofNullable(endpoint).filter(String::isBlank).orElse("upcoming");
        String url = this.formatUrl("/movies/"+endpoint);
        TMDBResult r = restTemplate.getForObject(url, TMDBResult.class);
        return CompletableFuture.completedFuture(r.getResults());
    }

    public String formatUrl(String endpoint) {
        return String.format("%s%s?api_key=%s&language=en-US&page=1", env.getApiBaseUrl(), endpoint, env.getApiKey());
    }
}
