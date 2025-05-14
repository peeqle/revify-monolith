package com.revify.monolith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
@EntityScan(basePackages = {"com.revify.monolith"})
public class RevifyMonolithApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevifyMonolithApplication.class, args);
    }

}
