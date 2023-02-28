package com.ReRollBag.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
@Log4j2
public class FirebaseConfig {

    @PostConstruct
    public void init_firebase() {

        log.info("Load Firebase Key json file");
        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream("src/main/resources/Firebase-Admin-SDK-Key.json");
            //src/main/resources/Firebase-Admin-SDK-Key.json
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Firebase-Admin-SDK-Key.json is not able to find!", e);
        }


        log.info("Load Firebase Service Account");
        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Firebase Key's credential is wrong!", e);
        }

        // Check If App is Initialized before initializing
        if (!FirebaseApp.getApps().isEmpty()) FirebaseApp.initializeApp(options);
    }
}
