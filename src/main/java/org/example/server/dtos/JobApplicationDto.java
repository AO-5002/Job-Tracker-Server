package org.example.server.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.server.entities.StatusEnum;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class JobApplicationDto {

    private UUID id;
    @NotBlank(message = "Job title cannot be null.")
    private String job_title;
    @Size(min = 2, max = 50)
    private String company_name;
    private String location;
    @NotNull(message = "Status cannot be null.")
    private StatusEnum status;
    @NotBlank(message = "Job posting cannot be null.")
    @URL
    private String job_post_url;
    @URL
    private String resume_url;
    @URL
    private String cover_letter_url;
    private LocalDate created_at;
}
