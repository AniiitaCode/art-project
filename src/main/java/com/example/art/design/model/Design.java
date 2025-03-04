package com.example.art.design.model;

import com.example.art.history.model.History;
import com.example.art.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Design {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FormDesign form;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ConstructionDesign construction;

    @Column(nullable = false)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecorationPebbles pebbles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecorationPicture picture;

    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private History history;
}
