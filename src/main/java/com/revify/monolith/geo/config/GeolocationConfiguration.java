package com.revify.monolith.geo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class GeolocationConfiguration {

    private static final String NOMINATIM_MAP_API = "https://nominatim.openstreetmap.org";

    @Bean("nominatimWebClient")
    public WebClient nominatimWebClient() {
        return WebClient.builder()
                .baseUrl(NOMINATIM_MAP_API)
                .build();
    }
}
