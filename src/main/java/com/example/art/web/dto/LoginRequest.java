package com.example.art.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @Size(min = 3, max = 20, message = "Username length must be 3 and 20 characters!")
    @NotBlank(message = "Username cannot be empty!")
    private String username;

    @Size(min = 3, max = 20, message = "Password length must be 3 and 20 characters!")
    @NotBlank(message = "Password cannot be empty!")
    private String password;
}
