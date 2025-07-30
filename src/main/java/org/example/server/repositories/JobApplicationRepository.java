package org.example.server.repositories;

import org.example.server.entities.JobApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplicationEntity, UUID> {

}
