package com.revify.monolith.geo.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.geo.model.Place;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Semaphore;

@Slf4j
@Service
@RequiredArgsConstructor

/**
 * Geolocation OpenStreetMap API eating service
 */
public class NominatimService {

    private static final String REVERSE_PATH = "/reverse";

    private static final Semaphore sema = new Semaphore(5);

    @Autowired
    @Qualifier("nominatimWebClient")
    private WebClient webClient;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Mono<Place> readGeolocationAddress(Double latitude, Double longitude) {
        String finalPath = REVERSE_PATH + accelerateParticles(
                DefaultParticle.jsonFormatting()
                        .setNext(DefaultParticle.coordinates(latitude, longitude))
        );

        return request(finalPath).map(response -> gson.fromJson(response, Place.class));
    }

    /**
     * Attention RATE LIMITED TO 5 PS
     *
     * @param path
     * @return
     */
    private Mono<String> request(String path) {
        log.info("Searching for {}", path);
        return Mono.fromCallable(sema::tryAcquire)
                .delayElement(Duration.ofSeconds(1))
                .flatMap(i ->
                        webClient.get()
                                .uri(path)
                                .retrieve()
                                .bodyToMono(String.class)
                                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))))
                .doFinally(s -> sema.release());
    }

    private interface SearchParticle<T> extends Particle {
        T value();

        SearchParticle next();
    }

    private interface Particle {
        String name();

        String FORMAT_NAME = "format";
        String POLYGON_KML = "polygon_kml";
        String ADDRESS_DETAILS_NAME = "addressdetails";
        String QUERY_NAME = "q";
        String LATITUDE_NAME = "lat";
        String LONGITUDE_NAME = "lon";


        String FORMAT_JSON_VALUE = "json";
        String FORMAT_XML_VALUE = "xml";
    }

    private static class DefaultParticle<T> implements SearchParticle<T> {

        private final String name;
        private final T value;
        private SearchParticle next;

        private DefaultParticle(String name, T value) {
            this.name = name;
            this.value = value;
        }

        public DefaultParticle<T> setNext(SearchParticle next) {
            this.next = next;
            return this;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public T value() {
            return this.value;
        }

        @Override
        public SearchParticle next() {
            return this.next;
        }

        @Override
        public String toString() {
            return name() + "=" + value();
        }

        public static DefaultParticle<String> jsonFormatting() {
            return new DefaultParticle<>(FORMAT_NAME, FORMAT_JSON_VALUE);
        }

        public static DefaultParticle<Double> coordinates(Double latitude, Double longitude) {
            return DefaultParticle.initial(LATITUDE_NAME, latitude)
                    .setNext(DefaultParticle.initial(LONGITUDE_NAME, longitude));
        }

        public static <Z> DefaultParticle<Z> initial(String name, Z value) {
            return new DefaultParticle<>(name, value);
        }
    }

    private static String accelerateParticles(SearchParticle particle) {
        String delimiter = "";
        StringBuilder accelerateParticles = new StringBuilder("?");

        SearchParticle currentParticle = particle;
        while (currentParticle != null) {
            accelerateParticles.append(delimiter);
            accelerateParticles.append(currentParticle);

            currentParticle = currentParticle.next();
            delimiter = "&";
        }

        return accelerateParticles.toString();
    }
}
