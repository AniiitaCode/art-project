package com.example.art.user.model;

import com.example.art.design.model.Design;
import com.example.art.history.model.History;
import com.example.art.wallet.model.Wallet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false, unique = true)
    private String email;

    private String profilePicture;

    private LocalDateTime createdOn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private LocalDateTime updatedOn;

    @OneToOne
    private Wallet wallet;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<History> histories;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Design> designs;
}
