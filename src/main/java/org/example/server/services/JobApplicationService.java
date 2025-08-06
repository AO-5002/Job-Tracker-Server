package org.example.server.services;

import lombok.RequiredArgsConstructor;
import org.example.server.dtos.JobApplicationDto;
import org.example.server.dtos.UpdateJobApplicationDto;
import org.example.server.entities.JobApplicationEntity;
import org.example.server.entities.StatusEnum;
import org.example.server.entities.UserEntity;
import org.example.server.exceptions.ApplicationException;
import org.example.server.exceptions.file.FileNotValid;
import org.example.server.exceptions.job_application.ApplicationNotFound;
import org.example.server.exceptions.job_application.ForbiddenApplicationAccess;
import org.example.server.exceptions.job_application.NoApplicationsFound;
import org.example.server.exceptions.user.UserNotFoundException;
import org.example.server.mappers.JobApplicationMapper;
import org.example.server.repositories.JobApplicationRepository;
import org.example.server.repositories.UserRepository;
import org.springframework.http.HttpStatus;
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
            if (isValidFile(resumeFile)) {
                s3Service.uploadFile(resumeFile, "resume_folder/");
                String resumeFileName = resumeFile.getOriginalFilename();
                String folderLocationFileResume = "resumes/" + resumeFileName;
                newApplication.setResume_url(folderLocationFileResume);
            } else {
                throw new FileNotValid("Resume file is not valid");
            }
        }

        if (coverLetterFile != null && !coverLetterFile.isEmpty()) {
            if (isValidFile(coverLetterFile)) {
                s3Service.uploadFile(coverLetterFile, "jobcover_folder/");
                String coverLetterFileName = coverLetterFile.getOriginalFilename();
                String folderLocationFileCoverLetter = "cover_letters/" + coverLetterFileName;
                newApplication.setCover_letter_url(folderLocationFileCoverLetter);
            } else {
                throw new FileNotValid("Cover letter file is not valid");
            }
        }

        // 4) Check if the status is anything but SAVED to set the application date

        if(StatusEnum.valueOf(status) != StatusEnum.SAVED){
            newApplication.setApplication_date(LocalDateTime.now());
        }

        // 5) Save to the db

        jobApplicationRepository.save(newApplication);
        return jobApplicationMapper.jobEntityToJobDto(newApplication);
    }

    public JobApplicationDto updateApplication(
            String id,
            String authToken,
            String jobTitle,
            String companyName,
            String location,
            String status,
            String jobPostUrl,
            MultipartFile resumeFile,
            MultipartFile coverLetterFile
    ){

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
        if(jobTitle != null && !jobTitle.equals(returnedJob.getJob_title())) {
            returnedJob.setJob_title(jobTitle);
        }

        // Company name
        if(companyName != null && !companyName.equals(returnedJob.getCompany_name())) {
            returnedJob.setCompany_name(companyName);
        }

        // Location
        if(location != null && !location.equals(returnedJob.getLocation())) {
            returnedJob.setLocation(location);
        }

        // Status

        if (status != null && !status.isBlank()) {
            try {
                StatusEnum parsedStatus = StatusEnum.valueOf(status.trim());
                if (!parsedStatus.equals(returnedJob.getStatus())) {
                    returnedJob.setStatus(parsedStatus);

                    if (parsedStatus != StatusEnum.SAVED && returnedJob.getApplication_date() == null) {
                        returnedJob.setApplication_date(LocalDateTime.now());
                    }
                }
            } catch (IllegalArgumentException e) {
                throw new ApplicationException("Invalid status: " + status, HttpStatus.NOT_FOUND);
            }
        }

        // Job Post URL
        if(jobPostUrl != null && !jobPostUrl.equals(returnedJob.getJob_post_url())) {
            returnedJob.setJob_post_url(jobPostUrl);
        }

        // Resume URL & Cover Letter URL
        // 5. Extract & save the file names to the entity

        if (resumeFile != null && !resumeFile.isEmpty()) {
            if (isValidFile(resumeFile)) {
                s3Service.uploadFile(resumeFile, "resume_folder/");
                String resumeFileName = resumeFile.getOriginalFilename();
                String folderLocationFileResume = "resumes/" + resumeFileName;
                returnedJob.setResume_url(folderLocationFileResume);
            } else {
                throw new FileNotValid("Resume file is not valid");
            }
        }

        if (coverLetterFile != null && !coverLetterFile.isEmpty()) {
            if (isValidFile(coverLetterFile)) {
                s3Service.uploadFile(coverLetterFile, "jobcover_folder/");
                String coverLetterFileName = coverLetterFile.getOriginalFilename();
                String folderLocationFileCoverLetter = "cover_letters/" + coverLetterFileName;
                returnedJob.setCover_letter_url(folderLocationFileCoverLetter);
            } else {
                throw new FileNotValid("Cover letter file is not valid");
            }
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
