package org.example.server.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @Size(min = 2, max = 20)
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
    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate application_date;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate updated_at;
}
