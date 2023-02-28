package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.appservice.Create;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    public static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto addBooking(@RequestHeader(HEADER) Long userId, @Validated({Create.class}) @RequestBody BookingDtoCreate bookingDtoCreate) {
        return bookingService.addBooking(userId, bookingDtoCreate);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(HEADER) Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto ownerGetBooking(@RequestHeader(HEADER) Long userId,
                                      @PathVariable Long bookingId) {
        return bookingService.getBookingByBookingId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsForDefaultUser(@RequestHeader(HEADER) Long userId,
                                                      @RequestParam(defaultValue = "ALL", required = false) String state,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                                      @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        return bookingService.getBookingsForDefaultUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnersBookings(@RequestHeader(HEADER) Long userId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state,
                                              @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                              @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        return bookingService.findBookingsByOwner(userId, state, from, size);
    }
}