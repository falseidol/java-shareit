package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static BookingDto toDtoResponse(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto.BookingDtoBuilder bookingDto = BookingDto.builder();

        bookingDto.id(booking.getId());
        bookingDto.start(booking.getStart());
        bookingDto.end(booking.getEnd());
        bookingDto.item(booking.getItem());
        bookingDto.booker(booking.getBooker());
        bookingDto.status(booking.getStatus());

        return bookingDto.build();
    }

    public static BookingDtoShort toDtoShort(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDtoShort.BookingDtoShortBuilder bookingDtoShort = BookingDtoShort.builder();

        bookingDtoShort.bookerId(bookingBookerId(booking));
        bookingDtoShort.id(booking.getId());

        return bookingDtoShort.build();
    }

    public static Long bookingBookerId(Booking booking) {
        if (booking == null) {
            return null;
        }
        User booker = booking.getBooker();
        if (booker == null) {
            return null;
        }
        Long id = booker.getId();
        if (id == null) {
            return null;
        }
        return id;
    }
}