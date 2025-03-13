package com.example.art.email.client;

import com.example.art.email.client.dto.EmailPreference;
import com.example.art.email.client.dto.EmailRequest;
import com.example.art.email.client.dto.UpsertPreference;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@FeignClient(name = "email-srv", url = "http://localhost:8081/api/v1/emails")
public interface EmailClient {


    @PostMapping("/preferences")
    ResponseEntity<Void> upsertPreference(@RequestBody UpsertPreference upsertPreference);

    @GetMapping("/preferences")
    ResponseEntity<EmailPreference> getEmailPreference(@RequestParam(name = "userId") UUID userId);

    @PostMapping
    ResponseEntity<Void> sendEmail(@RequestBody EmailRequest emailRequest);

    @PutMapping("/preferences")
    ResponseEntity<Void> changePreference(@RequestParam(name = "userId") UUID userId,
                                          @RequestParam(name = "enabled") boolean enabled);

}
