package com.revify.monolith.geo;

import com.revify.monolith.geo.model.Place;
import com.revify.monolith.geo.service.GeolocationService;
import com.revify.monolith.geo.service.NominatimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/geo")
@RequiredArgsConstructor
public class GeolocationResource {

    private final NominatimService nominatimService;

    private final GeolocationService geolocationService;

    @GetMapping("/resolve")
    public Place resolve(@RequestParam double lat, @RequestParam double lng) {
        return nominatimService.readGeolocationAddress(lat, lng);
    }

    @PostMapping("/store")
    public void storeCurrentUserLocation(@RequestParam double lat, @RequestParam double lng) {
        geolocationService.updateUserGeolocation(lat, lng);
    }
}
