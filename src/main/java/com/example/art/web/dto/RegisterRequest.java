package com.example.art.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Size(min = 3, max = 20, message = "First name length must be 3 and 20 characters!")
    @NotBlank(message = "First name cannot be empty!")
    private String firstName;

    @Email(message = "Enter valid email!")
    @NotBlank(message = "Email cannot be empty!")
    private String email;

    @Size(min = 3, max = 20, message = "Username length must be 3 and 20 characters!")
    @NotBlank(message = "Username cannot be empty!")
    private String username;

    @Size(min = 3, max = 20, message = "Password length must be 3 and 20 characters!")
    @NotBlank(message = "Password cannot be empty!")
    private String password;

    @Size(min = 3, max = 20, message = "Password length must be 3 and 20 characters!")
    @NotBlank(message = "Password cannot be empty!")
    private String confirmPassword;
}
