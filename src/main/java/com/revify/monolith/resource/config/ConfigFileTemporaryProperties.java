package com.revify.monolith.resource.config;

import com.revify.monolith.config.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "server.files.temporary")
@PropertySource(value = "classpath:files.yml", factory = YamlPropertySourceFactory.class)
public class ConfigFileTemporaryProperties {
    private String directory;

    private Long maxSize = 1024L;
}
