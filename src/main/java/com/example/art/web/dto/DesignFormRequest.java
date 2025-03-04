package com.example.art.web.dto;

import com.example.art.design.model.ConstructionDesign;
import com.example.art.design.model.FormDesign;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DesignFormRequest {

    @NotNull(message = "Select form")
    private FormDesign form;

    @NotNull(message = "Select construction")
    private ConstructionDesign construction;
}
