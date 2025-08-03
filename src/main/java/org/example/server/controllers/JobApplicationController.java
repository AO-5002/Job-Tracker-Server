package org.example.server.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.dtos.JobApplicationDto;
import org.example.server.dtos.UpdateJobApplicationDto;
import org.example.server.services.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/applications")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class JobApplicationController {

    private final JobApplicationService  jobApplicationService;

    // API's below:

    @GetMapping
    private ResponseEntity<List<JobApplicationDto>> getJobApplications(Authentication auth) {

        String userAuth = auth.getName();
        List<JobApplicationDto> listedApplications = jobApplicationService.getJobApplications(userAuth);
        return ResponseEntity.ok(listedApplications);
    }

    @GetMapping("/{id}")
    private ResponseEntity<JobApplicationDto> getJobApplication(@PathVariable("id") String id, Authentication auth) {

        String authToken = auth.getName();
        JobApplicationDto jobApplicationDto = jobApplicationService.getJobApplication(id, authToken);

        return ResponseEntity.ok(jobApplicationDto);
    }

    @PostMapping
    private ResponseEntity<JobApplicationDto> createApplication(
            @RequestParam(name =  "job_title", required = true) String jobTitle,
            @RequestParam(name = "company_name", required = true) String companyName,
            @RequestParam(name = "location", required = false) String location,
            @RequestParam(name = "status", required = true) String status,
            @RequestParam(name = "job_post_url") String jobPostUrl,
            @RequestParam(value = "resume_file", required = false) MultipartFile resumeFile,
            @RequestParam(value = "cover_letter_file", required = false) MultipartFile coverLetterFile,
            Authentication auth
    ) {
        String authToken = auth.getName();
        JobApplicationDto createdApplication = jobApplicationService.createApplication(
                authToken,
                jobTitle,
                companyName,
                location,
                status,
                jobPostUrl,
                resumeFile,
                coverLetterFile
        );

        return ResponseEntity.status(201).body(createdApplication);
    }

    @PatchMapping("/{id}")
    private ResponseEntity<JobApplicationDto> updateApplication(@PathVariable("id") String id, @Valid @RequestBody UpdateJobApplicationDto newApplication, Authentication auth){
        String authToken = auth.getName();
        JobApplicationDto response = jobApplicationService.updateApplication(id, authToken, newApplication);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteApplication(@PathVariable("id") String id, Authentication auth){
        String authToken = auth.getName();
        jobApplicationService.deleteApplication(id, authToken);
        return ResponseEntity.status(204).build();
    }
}
