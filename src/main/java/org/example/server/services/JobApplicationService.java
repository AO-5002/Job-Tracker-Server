package org.example.server.services;

import lombok.RequiredArgsConstructor;
import org.example.server.dtos.JobApplicationDto;
import org.example.server.dtos.UpdateJobApplicationDto;
import org.example.server.entities.JobApplicationEntity;
import org.example.server.entities.StatusEnum;
import org.example.server.entities.UserEntity;
import org.example.server.exceptions.file.FileNotValid;
import org.example.server.exceptions.job_application.ApplicationNotFound;
import org.example.server.exceptions.job_application.ForbiddenApplicationAccess;
import org.example.server.exceptions.job_application.NoApplicationsFound;
import org.example.server.exceptions.user.UserNotFoundException;
import org.example.server.mappers.JobApplicationMapper;
import org.example.server.repositories.JobApplicationRepository;
import org.example.server.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final UserRepository userRepository;

    private final S3Service s3Service;

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

    private boolean isValidFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // Check file extension
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("txt", "pdf", "doc", "docx");

        if (!allowedExtensions.contains(extension)) {
            return false;
        }

        // Check file size (e.g., max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return false;
        }

        // Add any other validation rules you need
        return true;
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

    public JobApplicationDto createApplication(
            String authToken,
            String jobTitle,
            String companyName,
            String location,
            String status,
            String jobPostUrl,
            MultipartFile resumeFile,
            MultipartFile coverLetterFile
    ) {

        // 1. Get the user based on auth token

        UserEntity user = getUserBasedOnAuth(authToken);


        System.out.println("Received parameters:");
        System.out.println("jobTitle: '" + jobTitle + "'");
        System.out.println("companyName: '" + companyName + "'");
        System.out.println("location: '" + location + "'");
        System.out.println("status: '" + status + "'");
        System.out.println("jobPostUrl: '" + jobPostUrl + "'");

        // 2) Convert those params into an entity (except for the file stuff)

        JobApplicationEntity newApplication =  new JobApplicationEntity();
        newApplication.setUser(user);
        newApplication.setJob_title(jobTitle);
        newApplication.setCompany_name(companyName);
        newApplication.setLocation(location);
        StatusEnum statusEnumValue = StatusEnum.valueOf(status);
        newApplication.setStatus(statusEnumValue);
        newApplication.setJob_post_url(jobPostUrl);

        // 3) Extract & save the file names to the entity

        if (resumeFile != null && !resumeFile.isEmpty()) {
            // File was provided, so validate and process it
            if (isValidFile(resumeFile)) {
                s3Service.uploadFile(resumeFile);
                String resumeFileName = resumeFile.getOriginalFilename();
                newApplication.setResume_url(resumeFileName);
            } else {
                throw new FileNotValid("Resume file is not valid");
            }
        } else {
            // No file provided - that's okay, just set to null
            newApplication.setResume_url(null);
        }

        if (coverLetterFile != null && !coverLetterFile.isEmpty()) {
            // File was provided, so validate and process it
            if (isValidFile(coverLetterFile)) {
                s3Service.uploadFile(coverLetterFile);
                String coverLetterFileName = coverLetterFile.getOriginalFilename();
                newApplication.setCover_letter_url(coverLetterFileName);
            } else {
                throw new FileNotValid("Cover letter file is not valid");
            }
        } else {
            // No file provided - that's okay, just set to null
            newApplication.setCover_letter_url(null);
        }

        // 4) Check if the status is anything but SAVED to set the application date

        if(StatusEnum.valueOf(status) != StatusEnum.SAVED){
            newApplication.setApplication_date(LocalDateTime.now());
        }

        // 5) Save to the db

        jobApplicationRepository.save(newApplication);
        return jobApplicationMapper.jobEntityToJobDto(newApplication);
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
