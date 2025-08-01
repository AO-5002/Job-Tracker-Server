package org.example.server.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "job_application")
public class JobApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "id",
            unique = true,
            nullable = false
    )
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id")
    private UserEntity user;


    @Column(
            name = "job_title",
            nullable = false
    )
    private String job_title;


    @Column(
            name = "company_name",
            nullable = false
    )
    private String company_name;


    @Column(
            name = "location"
    )
    private String location;


    @Column(
            name = "status",
            nullable = false
    )
    private StatusEnum status;


    @Column(
            name = "application_date",
            nullable = false
    )
    @JsonFormat(
            shape =  JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @CreationTimestamp
    private LocalDate application_date;

    @Column(
            name = "job_post_url",
            nullable = false
    )
    private String job_post_url;

    // S3 Related
    private String cover_letter_url;
    private String resume_url;


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
    private LocalDateTime created_at;


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
    private LocalDateTime  updated_at;
}
