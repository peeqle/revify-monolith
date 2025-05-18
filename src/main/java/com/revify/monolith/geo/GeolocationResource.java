package com.revify.monolith.geo;

import com.revify.monolith.geo.model.Place;
import com.revify.monolith.geo.service.NominatimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/geo")
@RequiredArgsConstructor
public class GeolocationResource {

    private final NominatimService nominatimService;

    @GetMapping("/resolve")
    public Place resolve(@RequestParam double lat, @RequestParam double lng) {
        return nominatimService.readGeolocationAddress(lat, lng);
    }
}
