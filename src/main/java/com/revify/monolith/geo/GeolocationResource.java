package com.revify.monolith.geo;

import com.revify.monolith.geo.model.GeoLocation;
import com.revify.monolith.geo.service.GeolocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/geo")
@RequiredArgsConstructor
public class GeolocationResource {

    private final GeolocationService geolocationService;

    @GetMapping("/resolve")
    public GeoLocation resolve(@RequestParam double lat, @RequestParam double lng) {
        return geolocationService.resolveLocation(lat, lng);
    }

    @PostMapping("/store")
    public void storeCurrentUserLocation(@RequestParam double lat, @RequestParam double lng) {
        geolocationService.updateUserGeolocation(lat, lng);
    }
}
