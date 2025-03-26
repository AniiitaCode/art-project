package com.example.art.web.dto;

import com.example.art.design.model.Design;
import com.example.art.order.model.PaymentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class OrderRequest {

    private Design design;

    @NotNull(message = "Enter date")
    @Future(message = "Date must be future")
    private LocalDate savedDate;

    @NotNull(message = "Enter time")
    private LocalTime savedHour;

    @NotNull(message = "Enter payment method")
    private PaymentType paymentType;

}
