package org.example.server.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "id",
            nullable = false,
            unique = true
    )
    private UUID id;
    @Column(
            name = "auth0_id",
            nullable = false,
            unique = true
    )
    private String auth0_id;
    @Column(
            name = "email",
            nullable = false,
            unique = true
    )
    private String email;
    @Column(
            name = "name",
            nullable = false
    )
    private String name;
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    @JsonFormat(
            shape =  JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @CreationTimestamp
    private LocalDate created_at;
    @Column(
            name = "updated_at",
            nullable = false,
            updatable = true
    )
    @JsonFormat(
            shape =  JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @UpdateTimestamp
    private LocalDate updated_at;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplicationEntity> jobApplications = new ArrayList<>();
}
