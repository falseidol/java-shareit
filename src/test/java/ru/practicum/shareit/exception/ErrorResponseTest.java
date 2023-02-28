package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {
String message = "msg";
ErrorResponse errorResponse = new ErrorResponse(message);
    @Test
    void getErrorTest() {
        assertEquals(errorResponse.getError(),(message));
    }
}