package com.example.art.order.model;

import com.example.art.design.model.Design;
import com.example.art.schedule.model.Schedule;
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

    private boolean isAccept;

    @ManyToOne
    private Design design;

    @ManyToOne
    private Schedule schedule;
}
