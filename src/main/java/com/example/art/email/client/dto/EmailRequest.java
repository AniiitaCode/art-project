package com.example.art.email.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class EmailRequest {

    private UUID userId;

    private String subject;

    private String body;

}
