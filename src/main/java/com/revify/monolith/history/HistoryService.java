package com.revify.monolith.history;

import com.revify.monolith.history.model.HistoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Slf4j

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final MongoTemplate mongoTemplate;

    public void event(HistoryEvent event) {
        log.info("Processing system event: {}", event);
        mongoTemplate.save(event);
    }
}
