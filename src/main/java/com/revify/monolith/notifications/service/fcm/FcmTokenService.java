package com.revify.monolith.notifications.service.fcm;


import com.revify.monolith.notifications.domain.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<FirebaseToken> saveToken(FirebaseToken firebaseToken) {
        return findTokenByUserId(firebaseToken.getUserId())
                .flatMap(reactiveMongoTemplate::save);
    }

    public Mono<FirebaseToken> findTokenByUserId(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return reactiveMongoTemplate.find(query, FirebaseToken.class).single();
    }

    public Flux<FirebaseToken> findTokens(List<Long> userIds) {
        Query query = Query.query(Criteria.where("userId").in(userIds));
        return reactiveMongoTemplate.find(query, FirebaseToken.class);
    }
}
