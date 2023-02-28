package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    @Test
    void bookingBookerIdTestBookingIsNull() {
        Booking booking = null;
        assertEquals(BookingMapper.bookingBookerId(booking), null);
    }

    @Test
    void bookingBookerIdTestBookerIsNull() {
        User user = null;
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), null, user, BookingStatus.REJECTED);
        assertEquals(BookingMapper.bookingBookerId(booking), null);
    }

    @Test
    void bookingBookerIdTestUserIdIsNull() {
        User user = new User(null, "anton", "bebrov");
        Item item = new Item(1L, "bebr", "bebr", true, user, Request.builder().build());
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), null, user, BookingStatus.REJECTED);
        assertEquals(BookingMapper.bookingBookerId(booking), null);
    }
}