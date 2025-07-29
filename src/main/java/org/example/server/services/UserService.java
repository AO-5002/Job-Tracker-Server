package org.example.server.services;

import org.example.server.dtos.UserDto;
import org.example.server.exceptions.user.UserNotFoundException;
import org.example.server.mappers.UserMapper;
import org.example.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    // Method/s Below:

    // This creates a user using the @auth and @userDtoParam,
    // where auth is the token and userDtoParam is the new user.

    public UserDto createUser(String auth, UserDto userDtoParam) {

        var dtoToUser = userMapper.userDtoToUser(userDtoParam);
        dtoToUser.setAuth0_id(auth);
        userRepository.save(dtoToUser);
        return userMapper.userToUserDto(dtoToUser);
    }
}
