package org.example.server.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.dtos.JobApplicationDto;
import org.example.server.dtos.UpdateJobApplicationDto;
import org.example.server.services.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    private ResponseEntity<JobApplicationDto> createApplication(@Valid @RequestBody JobApplicationDto newApplication, Authentication auth){
        String authToken = auth.getName();
        JobApplicationDto createdApplication = jobApplicationService.createApplication(authToken, newApplication);

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
