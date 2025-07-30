package org.example.server.services;

import lombok.RequiredArgsConstructor;
import org.example.server.dtos.UserDto;
import org.example.server.exceptions.user.UserAlreadyExistsException;
import org.example.server.mappers.UserMapper;
import org.example.server.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private UserMapper userMapper;

    // Method/s Below:

    // This creates a user using the @auth and @userDtoParam,
    // where auth is the token and userDtoParam is the new user.

    public UserDto createUser(String auth, UserDto userDtoParam) {

        // First check if the user exists already.

        if (userRepository.existsByAuth0_id(auth)) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        var dtoToUser = userMapper.userDtoToUser(userDtoParam);
        dtoToUser.setAuth0_id(auth);
        userRepository.save(dtoToUser);
        return userMapper.userToUserDto(dtoToUser);
    }
}
