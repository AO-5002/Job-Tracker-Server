package org.example.server.mappers;

import org.example.server.dtos.JobApplicationDto;
import org.example.server.entities.JobApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobApplicationMapper {

    JobApplicationDto jobEntityToJobDto(JobApplicationEntity job);

//    @Mapping(target = "id", ignore = true)           // Let JPA auto-generate the ID
//    @Mapping(target = "user", ignore = true)         // You set this manually in service
//    @Mapping(target = "created_at", ignore = true)   // Let @CreationTimestamp handle this
//    @Mapping(target = "updated_at", ignore = true)   // Let @UpdateTimestamp handle this
    JobApplicationEntity jobDtoToJobEntity(JobApplicationDto jobDto);
}
