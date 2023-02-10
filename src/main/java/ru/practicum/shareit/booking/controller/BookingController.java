package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.dto.Create;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @Validated({Create.class}) @RequestBody BookingDtoCreate bookingDtoCreate) {
        return bookingService.addBooking(userId, bookingDtoCreate);
    }

    @PatchMapping("{/bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("{/bookingId}")
    public BookingDto ownerGetBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingByBookingId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsForDefaultUser(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsForDefaultUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnersBookings(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.findBookingsByOwner(userId, state);
    }
}