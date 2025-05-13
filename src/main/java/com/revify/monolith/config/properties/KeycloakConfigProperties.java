package com.revify.monolith.config.properties;

import com.revify.monolith.config.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource(value = "classpath:keycloak.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = KeycloakConfigProperties.PREFIX)
public class KeycloakConfigProperties {
    public static final String PREFIX = "keycloak";

    private String realm;
    private String resource;
    private String authServerUrl;
    private String sslRequired;
    private String username;
    private String password;
    private Credentials credentials;
    private boolean useResourceRoleMappings;
    private boolean publicClient;

    @Data
    public static class Credentials {
        private String secret;
    }
}

