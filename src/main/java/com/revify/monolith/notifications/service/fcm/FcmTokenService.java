package com.revify.monolith.notifications.service.fcm;


import com.revify.monolith.notifications.domain.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final MongoTemplate mongoTemplate;

    public FirebaseToken saveToken(FirebaseToken firebaseToken) {
        FirebaseToken tokenByUserId = findTokenByUserId(firebaseToken.getUserId());
        if (tokenByUserId != null) {
            return mongoTemplate.save(firebaseToken);
        }
        return firebaseToken;
    }

    public FirebaseToken findTokenByUserId(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.findOne(query, FirebaseToken.class);
    }

    public List<FirebaseToken> findTokens(List<Long> userIds) {
        Query query = Query.query(Criteria.where("userId").in(userIds))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, FirebaseToken.class);
    }
}
