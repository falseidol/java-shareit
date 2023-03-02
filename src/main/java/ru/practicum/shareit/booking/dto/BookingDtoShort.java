package ru.practicum.shareit.booking.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDtoShort {
    private Long id;
    private Long bookerId;
}