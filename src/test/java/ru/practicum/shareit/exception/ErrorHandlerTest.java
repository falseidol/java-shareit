package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ErrorHandlerTest {
    UserNotFoundException userNotFoundException = new UserNotFoundException("User not found");
    ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("Object not found");
    ErrorResponse errorResponse = new ErrorResponse(userNotFoundException.getMessage());

    @Test
    void handleUserNotFoundException() {
        assertEquals(errorResponse.getError(), ("User not found"));
    }

    @Test
    void handleObjectNotFoundException() {
        assertEquals(objectNotFoundException.getMessage(), ("Object not found"));
    }
}