package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ErrorHandlerTest {
    UserNotFoundException userNotFoundException = new UserNotFoundException("User not found");
    ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("Object not found");
    BadRequestException badRequestException = new BadRequestException("BadRequest");
    ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleUserNotFoundException() {
        ErrorResponse errorResponse = new ErrorResponse("User not found");
        assertEquals(errorHandler.handleUserNotFoundException(userNotFoundException), errorResponse);
    }

    @Test
    void handleObjectNotFoundException() {
        ErrorResponse errorResponse = new ErrorResponse("Object not found");
        assertEquals(errorHandler.handleObjectNotFoundException(objectNotFoundException), errorResponse);
    }

    @Test
    void handleBadRequestException() {
        ErrorResponse errorResponse = new ErrorResponse("BadRequest");
        assertEquals(errorHandler.handleBadRequestException(badRequestException), errorResponse);
    }
}