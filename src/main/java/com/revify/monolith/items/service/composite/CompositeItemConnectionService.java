package com.revify.monolith.items.service.composite;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.model.item.composite.CompositeItem;
import com.revify.monolith.items.model.item.composite.CompositeItemRequestLink;
import com.revify.monolith.items.model.item.composite.CompositeItemUserConnectionRequest;
import com.revify.monolith.items.service.item.ItemReadService;
import com.revify.monolith.notifications.models.Notification;
import com.revify.monolith.notifications.models.NotificationType;
import com.revify.monolith.notifications.service.NotificationService;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.ReadUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CompositeItemConnectionService {

    private final MongoTemplate mongoTemplate;

    private final ItemReadService itemReadService;

    private final ReadUserService readUserService;

    private final CompositeItemService compositeItemService;

    private final NotificationService notificationService;

    public void sendInvitations(List<Long> userIds, String itemId) {
        CompositeItem compositeItem = compositeItemService.findForItem(itemId);
        if (compositeItem == null) {
            compositeItem = compositeItemService.createCompositeInstance(itemId);
        }


        final CompositeItem finalCompositeItem = compositeItem;

        List<CompositeItemUserConnectionRequest> requests = new ArrayList<>();
        readUserService.getCurrentUser().ifPresent(user -> {
            Set<Long> collect = user.getFriends().stream().map(AppUser::getId).collect(Collectors.toSet());

            userIds.stream().filter(collect::contains).forEach(id -> {
                CompositeItemUserConnectionRequest request = new CompositeItemUserConnectionRequest();
                request.setUserId(UserUtils.getUserId());
                request.setCompositeItem(finalCompositeItem);
                request.setUserId(id);

                requests.add(request);
            });

            {
                //send notifications to involved users
                Notification notification = Notification.builder()
                        .type(NotificationType.COMPOSITION)
                        .relatedUserId(UserUtils.getUserId())
                        .relatedUsers(collect)
                        .relatedCompositeItemId(finalCompositeItem.getId().toHexString())
                        .title("Invitation")
                        .body("Composite Item " + finalCompositeItem.getId().toHexString())
                        .build();

                notificationService.createNotification(notification);
            }
        });

        mongoTemplate.insertAll(requests);
    }

    public CompositeItemRequestLink createInvitationLink(Long validUntil, String itemId) {
        Item byId = itemReadService.findById(itemId);

        if (byId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }

        CompositeItem compositeItem = compositeItemService.findForItem(itemId);
        if (compositeItem == null) {
            compositeItem = compositeItemService.createCompositeInstance(itemId);
        }


        CompositeItemRequestLink link = findForCompositeItem(compositeItem.getId().toHexString());
        if (link == null) {
            link = CompositeItemRequestLink.builder()
                    .validUntil(validUntil == null ? Instant.now().plus(3, ChronoUnit.DAYS).toEpochMilli() : validUntil)
                    .compositeItem(compositeItem)
                    .isActive(true)
                    .hashKey(generateHash())
                    .build();

            link = mongoTemplate.save(link);
        }
        return link;
    }

    public void deleteInvitationLinksForItem(String itemId) {
        for (CompositeItemRequestLink compositeItemRequestLink : findForItem(itemId)) {
            mongoTemplate.remove(compositeItemRequestLink);
        }
    }

    public void connectViaLink(String code) {
        CompositeItemRequestLink byHash = findByHash(code);
        if (byHash == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Link is not found");
        }

        CompositeItem compositeItem = byHash.getCompositeItem();
        if (compositeItem == null || compositeItem.getId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Composite item is not found");
        }
        if (compositeItem.getCreatorId() == UserUtils.getUserId()) {
            return;
        }
        mongoTemplate.save(compositeItem.addUserConnected(UserUtils.getUserId()));
    }

    public List<CompositeItemRequestLink> findForItem(String itemId) {
        Query query = new Query(Criteria.where("compositeItem.initialItemId").is(itemId)
                .and("isActive").is(true));
        return mongoTemplate.find(query, CompositeItemRequestLink.class);
    }

    public CompositeItemRequestLink findForCompositeItem(String compositeItemId) {
        Query query = Query.query(Criteria.where("compositeItem._id").is(compositeItemId)
                .and("isActive").is(true)
                .and("validUntil").gte(Instant.now().toEpochMilli()));
        return mongoTemplate.findOne(query, CompositeItemRequestLink.class);
    }

    public CompositeItemRequestLink findByHash(String hash) {
        Query query = Query.query(Criteria.where("hashKey").is(hash).and("isActive").is(true));
        return mongoTemplate.findOne(query, CompositeItemRequestLink.class);
    }

    public static String generateHash() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
