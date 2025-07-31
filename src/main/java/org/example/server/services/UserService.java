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

    public void createUser(String auth, UserDto userDtoParam) {

        // 1.) First check if the user exists already.

        if (userRepository.existsByAuth0_id(auth)) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        // 2. Then, map the dto into a user entity & set the auth token

        var dtoToUser = userMapper.userDtoToUser(userDtoParam);
        dtoToUser.setAuth0_id(auth);

        // 3.) Save the user to the db

        userRepository.save(dtoToUser);
    }
}