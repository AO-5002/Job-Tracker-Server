package org.example.server.mappers;

import org.example.server.dtos.JobApplicationDto;
import org.example.server.entities.JobApplicationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobApplicationMapper {

    JobApplicationDto jobEntityToJobDto(JobApplicationEntity job);
    JobApplicationEntity jobDtoToJobEntity(JobApplicationDto jobDto);
}
