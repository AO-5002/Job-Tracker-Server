package org.example.server.serviceTests;

import org.example.server.dtos.UserDto;
import org.example.server.entities.UserEntity;
import org.example.server.exceptions.user.UserAlreadyExistsException;
import org.example.server.mappers.UserMapper;
import org.example.server.repositories.UserRepository;
import org.example.server.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;


    @Test
    void shouldThrowException_WhenUserAlreadyExists() {
        // Arrange
        String auth0Id = "auth0|existing";
        UserDto userDto = new UserDto();

        // Explain here please
        when(userRepository.existsByAuth0_id(auth0Id)).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(auth0Id, userDto);
        });

        // Make sure that "save" does not happen whenever the exception is thrown
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldCreateUser_WhenUserDoesNotExist() {
        String auth0Id = "auth0|existing";
        UserDto userDto = new UserDto();
        UserEntity user = new UserEntity();

        when(userRepository.existsByAuth0_id(auth0Id)).thenReturn(false);
        when(userMapper.userDtoToUser(userDto)).thenReturn(user);

        // Act

        userService.createUser(auth0Id, userDto);

        // Assert

        verify(userRepository, times(1)).save(user);
        assertEquals(auth0Id, user.getAuth0_id());
    }
}

