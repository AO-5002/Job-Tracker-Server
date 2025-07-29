package org.example.server.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {

    @NotNull(message = "Email cannot be null.")
    @Email
    private String email;
    @NotNull(message = "Name cannot be null.")
    private String name;
}
