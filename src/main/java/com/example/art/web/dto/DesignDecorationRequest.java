package com.example.art.web.dto;

import com.example.art.design.model.DecorationPebbles;
import com.example.art.design.model.DecorationPicture;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DesignDecorationRequest {

    @Size(min = 2, max = 2, message = "The color number must be 2 digits!")
    @Pattern(regexp = "^(?:0[1-9]|[1-5][0-9]|60)$", message = "The color number must be a number between 01 and 60!")
    @NotBlank(message = "The color number cannot be empty!")
    private String color;

    @NotNull(message = "Select pebbles")
    private DecorationPebbles pebbles;

    @NotNull(message = "Select picture")
    private DecorationPicture picture;
}
