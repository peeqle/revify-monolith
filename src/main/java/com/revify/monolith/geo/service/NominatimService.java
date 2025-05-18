package com.revify.monolith.geo.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.geo.model.Place;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Semaphore;

@Slf4j
@Service
@RequiredArgsConstructor
public class NominatimService {

    private static final String NOMINATIM_MAP_API = "https://nominatim.openstreetmap.org";

    private static final String REVERSE_PATH = "/reverse";

    private final RestTemplate restTemplate;

    private static final Semaphore sema = new Semaphore(5);

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //todo read and propose address for input
    public Place readGeolocationAddress(Double latitude, Double longitude) {
        String finalPath = REVERSE_PATH + accelerateParticles(
                DefaultParticle.jsonFormatting()
                        .setNext(DefaultParticle.coordinates(latitude, longitude))
        );

        String response = request(finalPath);
        if (response != null) {
            return gson.fromJson(response, Place.class);
        }

        throw new RuntimeException(String.format("Cannot fetch location for %s, %s", latitude, longitude));
    }

    /**
     * Attention RATE LIMITED TO 5 PS
     *
     * @param path
     * @return
     */
    private String request(String path) {
        log.info("Searching for {}", path);
        try {
            sema.acquire();
            ResponseEntity<String> response = restTemplate.getForEntity(
                    NOMINATIM_MAP_API + path,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            sema.release();
        }
        return null;
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
