package org.example.server.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.server.entities.StatusEnum;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Data
public class UpdateJobApplicationDto {
    @Size(min = 2, max = 20)
    private String job_title;
    @Size(min = 2, max = 50)
    private String company_name;
    private String location;
    private StatusEnum status;
    @URL
    private String job_post_url;
    @URL
    private String resume_url;
    @URL
    private String cover_letter_url;
}
