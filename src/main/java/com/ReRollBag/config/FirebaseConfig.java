package com.ReRollBag.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Configuration
@Log4j2
@PropertySource("classpath:location.properties")
public class FirebaseConfig {

    @Value("${location.firebaseKey}")
    private String firebaseKeyLocation;

    @PostConstruct
    public void init_firebase() {

        log.info("Load Firebase Key json file");
        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream(firebaseKeyLocation);
            //src/main/resources/Firebase-Admin-SDK-Key.json
        } catch (FileNotFoundException e) {
            log.error("Firebase-Admin-SDK-Key.json is not able to find!");
            throw new RuntimeException("Firebase-Admin-SDK-Key.json is not able to find!", e);
        }


        log.info("Load Firebase Service Account");
        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://ReRollbag.firebaseio.com/")
                    .build();
        } catch (IOException e) {
            log.error("Firebase Key's credential is wrong!");
            throw new RuntimeException("Firebase Key's credential is wrong!", e);
        }

        // Check if Firebase app has already been initialized
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
        FirebaseApp defaultApp = null;
        for (FirebaseApp app : firebaseApps) {
            if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                defaultApp = app;
            }
        }
        if (defaultApp == null) {
            defaultApp = FirebaseApp.initializeApp(options);
        }
        FirebaseAuth.getInstance(defaultApp);
    }
}
