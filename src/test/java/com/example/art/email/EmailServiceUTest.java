package com.example.art.email;

import com.example.art.email.client.EmailClient;
import com.example.art.email.client.dto.EmailPreference;
import com.example.art.email.client.dto.EmailRequest;
import com.example.art.email.client.dto.UpsertPreference;
import com.example.art.email.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceUTest {

    @Mock
    private EmailClient emailClient;

    @Mock
    private Logger logg;

    @InjectMocks
    private EmailService emailService;


    @Test
    void savePreference_shouldCallEmailClientWithCorrectData_whenSuccess() {
        UUID userId = UUID.randomUUID();
        boolean isEnabled = true;
        String email = "test@example.com";
        UpsertPreference upsertPreference = UpsertPreference.builder()
                .userId(userId)
                .enabled(isEnabled)
                .contactEmail(email)
                .build();

        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();
        when(emailClient.upsertPreference(upsertPreference)).thenReturn(responseEntity);

        emailService.savePreference(userId, isEnabled, email);

        verify(emailClient).upsertPreference(upsertPreference);
        verifyNoInteractions(logg);
    }

    @Test
    void savePreference_shouldLogError_whenFailure() {

        UUID userId = UUID.randomUUID();
        boolean isEnabled = false;
        String email = "error@example.com";
        UpsertPreference upsertPreference = UpsertPreference.builder()
                .userId(userId)
                .enabled(isEnabled)
                .contactEmail(email)
                .build();

        ResponseEntity<Void> responseEntity = ResponseEntity.status(400).build();
        when(emailClient.upsertPreference(upsertPreference)).thenReturn(responseEntity);

        emailService.savePreference(userId, isEnabled, email);

        verify(emailClient).upsertPreference(upsertPreference);
    }

    @Test
    void whenGetPreferenceByUserId_thenThrowException() {
        UUID userId = UUID.randomUUID();

        EmailPreference emailPreference = new EmailPreference();
        emailPreference.setContactEmail("user@abv.bg");
        emailPreference.setEnabled(true);

        ResponseEntity<EmailPreference> response = ResponseEntity.status(400).build();

        when(emailClient.getEmailPreference(userId)).thenReturn(response);

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> {
                    emailService.getPreference(userId);
                });

        assertEquals("Email preference not found.", exception.getMessage());
    }

    @Test
    void whenGetPreferenceByUserId_thenSuccess() {
        UUID userId = UUID.randomUUID();

        EmailPreference emailPreference = new EmailPreference();
        emailPreference.setContactEmail("user@abv.bg");
        emailPreference.setEnabled(true);

        ResponseEntity<EmailPreference> response = ResponseEntity.status(200).build();

        when(emailClient.getEmailPreference(userId)).thenReturn(response);


        emailService.getPreference(userId);

        verify(emailClient, times(1)).getEmailPreference(userId);
    }

    @Test
    void changePreference_shouldCallEmailClient_whenSuccessful() {
        UUID userId = UUID.randomUUID();
        boolean enabled = true;

        ResponseEntity<Void> response = ResponseEntity.ok().build();
        when(emailClient.changePreference(userId, enabled)).thenReturn(response);

        emailService.changePreference(userId, enabled);

        verify(emailClient).changePreference(userId, enabled);

        verify(logg, never()).warn(anyString());
    }


    @Test
    void changePreference_shouldLogWarn_whenExceptionOccurs() {
        UUID userId = UUID.randomUUID();
        boolean enabled = false;

        doThrow(new RuntimeException("Email client error")).when(emailClient).changePreference(userId, enabled);

        emailService.changePreference(userId, enabled);

        verify(emailClient).changePreference(userId, enabled);
    }

    @Test
    void sendEmail_shouldCallEmailClient_whenSuccessful() {
        UUID userId = UUID.randomUUID();
        String subject = "Test Subject";
        String body = "Test Body";

        ResponseEntity<Void> response = ResponseEntity.ok().build();
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(response);

        emailService.sendEmail(userId, subject, body);

        verify(emailClient).sendEmail(any(EmailRequest.class));
        verify(logg, never()).error(anyString());
        verify(logg, never()).warn(anyString());
    }

    @Test
    void sendEmail_shouldLogError_whenResponseIsNot2xx() {
        UUID userId = UUID.randomUUID();
        String subject = "Test Subject";
        String body = "Test Body";

        ResponseEntity<Void> response = ResponseEntity.status(400).build();
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(response);

        emailService.sendEmail(userId, subject, body);

        verify(emailClient).sendEmail(any(EmailRequest.class));
        verify(logg, never()).warn(anyString());
    }

    @Test
    void sendEmail_shouldLogWarn_whenExceptionOccurs() {
        UUID userId = UUID.randomUUID();
        String subject = "Test Subject";
        String body = "Test Body";

        doThrow(new RuntimeException("Internal Server Error")).when(emailClient).sendEmail(any(EmailRequest.class));

        emailService.sendEmail(userId, subject, body);

        verify(emailClient).sendEmail(any(EmailRequest.class));
        verify(logg, never()).error(anyString());
    }
}









