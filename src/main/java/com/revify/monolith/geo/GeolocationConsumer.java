package com.revify.monolith.geo;

import com.revify.monolith.commons.messaging.KafkaTopic;
import com.revify.monolith.geo.service.GeolocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeolocationConsumer {

    private final GeolocationService geolocationService;

    @KafkaListener(topics = KafkaTopic.USER_GEOLOCATION_SYNC)
    public void listen(@Payload String record) {
        log.info("Received geolocation sync record: {}", record);
        geolocationService.saveParsedFromWS(record).subscribe(e -> log.info("Saved geolocation event: {}", e));
    }
}
