package com.example.art.email.service;

import com.example.art.email.client.EmailClient;
import com.example.art.email.client.dto.EmailPreference;
import com.example.art.email.client.dto.EmailRequest;
import com.example.art.email.client.dto.UpsertPreference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class EmailService {

    private final EmailClient emailClient;

    public EmailService(EmailClient emailClient) {
        this.emailClient = emailClient;
    }

    public void savePreference(UUID userId, boolean isEnabled, String email) {

        UpsertPreference upsertPreference = UpsertPreference.builder()
                .userId(userId)
                .enabled(isEnabled)
                .contactEmail(email)
                .build();

        ResponseEntity<Void> response =
                emailClient.upsertPreference(upsertPreference);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Can't save user preference for user with id [%s]"
                    .formatted(userId));
        }
    }

    public EmailPreference getPreference(UUID userId)  {

        ResponseEntity<EmailPreference> response =
                emailClient.getEmailPreference(userId);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Email preference not found.");
        }

        return response.getBody();
    }

    public void sendEmail(UUID userId, String subject, String body) {

        EmailRequest emailRequest = EmailRequest.builder()
                .userId(userId)
                .subject(subject)
                .body(body)
                .build();

        ResponseEntity<Void> response;

        try {
            response = emailClient.sendEmail(emailRequest);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Can't send email to user with id [%s].".formatted(userId));
            }
        }  catch (Exception e)  {
            log.warn("Can't send email due to 500 Internal Server Error.");
        }
    }

    public void changePreference(UUID userId, boolean enabled) {

        try {
            emailClient.changePreference(userId, enabled);
        }  catch (Exception e)  {
            log.warn("Can't change email preference.");
        }
    }
}
