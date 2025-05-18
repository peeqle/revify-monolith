package com.revify.monolith.geo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    private final Gson gson = new GsonBuilder().create();

    @KafkaListener(topics = KafkaTopic.USER_GEOLOCATION_SYNC)
    public void listen(@Payload String record) {
        log.info("Received geolocation sync record: {}", record);
        GeolocationService.GeolocationConsumerRecord geo = gson.fromJson(record, GeolocationService.GeolocationConsumerRecord.class);
        if (geo != null) {
            var payload = gson.fromJson(geo.getPayload(), GeolocationService.Payload.class);
            if (payload != null) {
                geolocationService.resolveGeolocation(geo.getSenderId(), geo.getTimestamp(), payload.getLat(), payload.getLon());
            }
        }
    }
}
