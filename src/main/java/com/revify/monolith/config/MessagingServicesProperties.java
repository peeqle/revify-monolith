package com.revify.monolith.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "messaging")
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class MessagingServicesProperties {

    private MessagingCredentials smsby;

    private MessagingCredentials vonage;

    @Data
    public static class MessagingCredentials {
        private String apiKey;
        private String apiSecret;
    }
}
