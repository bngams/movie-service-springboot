package fr.hiit.javatraining.cinema.movies.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.tmdb")
@Getter
@Setter
public class TMDBConfigProperties {
    String apiBaseUrl;
    String apiKey;
}
