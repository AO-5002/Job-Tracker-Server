package org.example.server.mappers;

import org.example.server.dtos.UserDto;
import org.example.server.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(UserEntity user);
    UserEntity userDtoToUser(UserDto userDto);
}
