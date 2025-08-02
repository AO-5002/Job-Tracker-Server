package org.example.server.services;

import lombok.RequiredArgsConstructor;
import org.example.server.dtos.JobApplicationDto;
import org.example.server.dtos.UpdateJobApplicationDto;
import org.example.server.entities.JobApplicationEntity;
import org.example.server.entities.StatusEnum;
import org.example.server.entities.UserEntity;
import org.example.server.exceptions.job_application.ApplicationNotFound;
import org.example.server.exceptions.job_application.ForbiddenApplicationAccess;
import org.example.server.exceptions.job_application.NoApplicationsFound;
import org.example.server.exceptions.user.UserNotFoundException;
import org.example.server.mappers.JobApplicationMapper;
import org.example.server.repositories.JobApplicationRepository;
import org.example.server.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final UserRepository userRepository;

    private final JobApplicationRepository jobApplicationRepository;

    private final JobApplicationMapper jobApplicationMapper;


    // Helper Methods below:

    // This method will either throw an exception or return the user if found based on Auth token.
    public UserEntity getUserBasedOnAuth(String authToken) {
        return userRepository.findByAuth0_id(authToken)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public JobApplicationEntity getJobApplicationById(String id) {
        UUID applicationUUID = UUID.fromString(id);
        return jobApplicationRepository.findById(applicationUUID).orElseThrow(() -> new ApplicationNotFound("Application not found."));
    }


    // API's below:

    public List<JobApplicationDto> getJobApplications(String authToken) {

        // 1. First verify user exists

        UserEntity user = getUserBasedOnAuth(authToken);

        // 2. Then check if they have applications
        List<JobApplicationEntity> applications = user.getJobApplications();

        if (applications.isEmpty()) {
            throw new NoApplicationsFound("No applications found for user");
        }

        // 3. Map and return
        return applications.stream()
                .map(jobApplicationMapper::jobEntityToJobDto)
                .toList();
    }


    public JobApplicationDto getJobApplication(String id, String authToken) {

        // 1. Check if the user exists

        UserEntity user = getUserBasedOnAuth(authToken);

        // 2. Check if the application they want to return even exists

        JobApplicationEntity returnedJob = getJobApplicationById(id);

        // 3. Check if the user auth matches the application auth

        if(!returnedJob.getUser().getAuth0_id().equals(user.getAuth0_id())) {
            throw new ForbiddenApplicationAccess("Application access has been denied");
        }

        // 4. Return the application

        return jobApplicationMapper.jobEntityToJobDto(returnedJob);
    }

    public JobApplicationDto createApplication(String authToken, JobApplicationDto newApplication) {

        // 1. Get the user based on auth token

        UserEntity user = getUserBasedOnAuth(authToken);

        // 2. Map the new application (dto) to the entity format

        JobApplicationEntity entityApplication = jobApplicationMapper.jobDtoToJobEntity(newApplication);

        // 3. Set the application to the associated user & save to the db

        entityApplication.setUser(user);
        JobApplicationEntity savedEntity = jobApplicationRepository.save(entityApplication);

        // 4. Check the status & set the application date

        if(savedEntity.getStatus() != StatusEnum.SAVED){
            savedEntity.setApplication_date(LocalDateTime.now());
        }

        // 5. Finally, return the saved entity

        return jobApplicationMapper.jobEntityToJobDto(savedEntity);
    }

    public JobApplicationDto updateApplication(String id, String authToken, UpdateJobApplicationDto newApplication){

        // 1. Check if the user exists.

        UserEntity user = getUserBasedOnAuth(authToken);

        // 2. Check if the application exists and return it.

        JobApplicationEntity returnedJob = getJobApplicationById(id);

        // 3. Check if the auth token matches the auth token in the application

        if(!returnedJob.getUser().getAuth0_id().equals(user.getAuth0_id())) {
            throw new ForbiddenApplicationAccess("Application access has been denied");
        }

        // 4. Update the fields by comparing the returned entity to the new application fields.

        // Job title
        if (newApplication.getJob_title() != null &&
                !newApplication.getJob_title().equals(returnedJob.getJob_title())) {
            returnedJob.setJob_title(newApplication.getJob_title());
        }

        // Company name
        if (newApplication.getCompany_name() != null &&
                !newApplication.getCompany_name().equals(returnedJob.getCompany_name())) {
            returnedJob.setCompany_name(newApplication.getCompany_name());
        }

        // Location
        if (newApplication.getLocation() != null &&
                !newApplication.getLocation().equals(returnedJob.getLocation())) {
            returnedJob.setLocation(newApplication.getLocation());
        }

        // Status
        if (newApplication.getStatus() != null &&
                !newApplication.getStatus().equals(returnedJob.getStatus())) {
            returnedJob.setStatus(newApplication.getStatus());

            // If the application has been moved from "SAVED" to anything else, set the application date.

            if(newApplication.getStatus() != StatusEnum.SAVED &&
                    returnedJob.getApplication_date() == null){
                returnedJob.setApplication_date(LocalDateTime.now());
            }
        }

        // Job Post URL
        if (newApplication.getJob_post_url() != null &&
                !newApplication.getJob_post_url().equals(returnedJob.getJob_post_url())) {
            returnedJob.setJob_post_url(newApplication.getJob_post_url());
        }

        // Resume URL
        if (newApplication.getResume_url() != null &&
                !newApplication.getResume_url().equals(returnedJob.getResume_url())) {
            returnedJob.setResume_url(newApplication.getResume_url());
        }

        // Cover Letter URL
        if (newApplication.getCover_letter_url() != null &&
                !newApplication.getCover_letter_url().equals(returnedJob.getCover_letter_url())) {
            returnedJob.setCover_letter_url(newApplication.getCover_letter_url());
        }

        // 5. Save to db & set the new date for the updated_at field.

        returnedJob.setUpdated_at(LocalDateTime.now());
        jobApplicationRepository.save(returnedJob);
        return jobApplicationMapper.jobEntityToJobDto(returnedJob);
    }

    public void deleteApplication(String id, String authToken){

        // 1. Check if the user exists

        UserEntity user = getUserBasedOnAuth(authToken);

        // 2. Check if the application exists

        JobApplicationEntity returnedJob = getJobApplicationById(id);

        // 3. Check if the application auth token matches the user token

        if(!user.getAuth0_id().equals(returnedJob.getUser().getAuth0_id())) {
            throw new ForbiddenApplicationAccess("Application access has been denied");
        }

        // 4. Delete the application

        jobApplicationRepository.delete(returnedJob);
    }



}
