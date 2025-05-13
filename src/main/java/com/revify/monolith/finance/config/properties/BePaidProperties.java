package com.revify.monolith.finance.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor

@ConfigurationProperties(prefix = "bepaid")
public class BePaidProperties {

    private String shopId;
    private String secretKey;
}
