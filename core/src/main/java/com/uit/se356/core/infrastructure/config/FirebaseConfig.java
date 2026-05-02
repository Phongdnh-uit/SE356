package com.uit.se356.core.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@RequiredArgsConstructor
@Configuration
@Profile({"render", "prod"})
public class FirebaseConfig {

  @Bean
  FirebaseApp firebaseApp() throws IOException {
    FirebaseOptions options =
        FirebaseOptions.builder().setCredentials(GoogleCredentials.getApplicationDefault()).build();

    return FirebaseApp.initializeApp(options);
  }
}
