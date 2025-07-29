package org.example.server.controllers;

import org.example.server.dtos.JobApplicationDto;
import org.example.server.mappers.JobApplicationMapper;
import org.example.server.repositories.JobApplicationRepository;
import org.example.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class JobApplicationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobApplicationMapper jobApplicationMapper;

    // Below are the APIs

//    @GetMapping
//    private ResponseEntity<List<JobApplicationDto>> getJobApplications() {
//
//
//
//    }
}
