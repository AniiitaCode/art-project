package com.example.art.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEditRequest {

    @Size(min = 3, max = 20, message = "First name length must be 3 and 20 characters!")
    private String firstName;

    @Email(message = "Enter valid email!")
    private String email;

    @URL(message = "Enter valid URL!")
    private String profilePicture;

    @Size(min = 3, max = 20, message = "Username length must be 3 and 20 characters!")
    private String username;
}


