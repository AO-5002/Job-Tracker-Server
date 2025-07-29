package org.example.server.exception_handlers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;

    // Add this constructor manually
    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
