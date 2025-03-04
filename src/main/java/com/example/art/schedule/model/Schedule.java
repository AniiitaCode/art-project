package com.example.art.schedule.model;

import com.example.art.order.model.Orders;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.EAGER)
    private List<Orders> orders;

}
