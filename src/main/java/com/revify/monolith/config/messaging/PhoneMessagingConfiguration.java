package com.revify.monolith.config.messaging;

import com.revify.monolith.config.MessagingServicesProperties;
import com.vonage.client.VonageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({MessagingServicesProperties.class})
public class PhoneMessagingConfiguration {

    private final MessagingServicesProperties messagingServicesProperties;

    @Value("${VONAGE_API_KEY:default-queue}")
    private String vonageApiKey;

    @Bean
    public VonageClient vonageClient() {
        return VonageClient.builder()
                .apiKey(messagingServicesProperties.getVonage().getApiKey())
                .apiSecret(messagingServicesProperties.getVonage().getApiSecret())
                .build();
    }
}
