package com.example.art.order.model;

import com.example.art.design.model.Design;
import com.example.art.history.model.History;
import com.example.art.schedule.model.Schedule;
import com.example.art.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDate savedDate;

    @Column(nullable = false)
    private LocalTime savedHour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    private boolean isAccept;

    @ManyToOne
    private User user;

    @ManyToOne
    private Design design;

    @ManyToOne
    private Schedule schedule;

    @ManyToOne
    private History history;
}
