package com.example.art.history.model;

import com.example.art.order.model.Orders;
import com.example.art.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private LocalDate addedOn;

    @OneToMany(mappedBy = "history", fetch = FetchType.EAGER)
    private List<Orders> orders;

    @ManyToOne
    private User user;
}
