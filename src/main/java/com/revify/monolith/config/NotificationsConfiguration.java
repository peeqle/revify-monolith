package com.revify.monolith.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class NotificationsConfiguration {
    @Bean
    public FirebaseOptions firebaseOptions() {
        try {
            ClassPathResource resource = new ClassPathResource("revify-key.json");
            return new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public FirebaseApp firebaseApp() {
        return FirebaseApp.initializeApp(firebaseOptions());
    }

}
