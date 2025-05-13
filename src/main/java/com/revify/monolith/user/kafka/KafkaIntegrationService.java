package com.revify.monolith.user.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.commons.models.chat.ChatRegistrationDTO;
import com.revify.monolith.user.models.user.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.revify.monolith.commons.messaging.KafkaTopic.CHAT_REGISTRATION;
import static com.revify.monolith.commons.messaging.KafkaTopic.USER_CONTEXT_ADD;


@Service
@RequiredArgsConstructor
public class KafkaIntegrationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final Gson gson = new GsonBuilder().create();

    public void sendKafkaUserContext(Map<String, String> contextElastic) {
        kafkaTemplate.send(USER_CONTEXT_ADD, gson.toJson(contextElastic));
    }

    public void createChatUser(AppUser appUser, String password) {
        kafkaTemplate.send(CHAT_REGISTRATION, new ChatRegistrationDTO(
                appUser.getUsername(),
                appUser.getEmail(),
                password,
                appUser.getCommonUserName()
        ));
    }
}
