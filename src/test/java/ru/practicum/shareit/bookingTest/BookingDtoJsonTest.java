package ru.practicum.shareit.bookingTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @SneakyThrows
    @Test
    void testUserDto() {
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.of(2023, Month.FEBRUARY, 27, 17, 2),
                LocalDateTime.of(2024, Month.FEBRUARY, 27, 17, 2), null, null, BookingStatus.WAITING);
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-02-27T17:02:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-02-27T17:02:00");
    }
}