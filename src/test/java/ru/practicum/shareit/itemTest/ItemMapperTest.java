package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemMapperTest {
    Item item = null;
    ItemDto itemDto = null;
    BookingDtoShort nextBooking;
    BookingDtoShort lastBooking;
    List<CommentDto> comments;

    @Test
    void toDtoTestIfItemNull() {
        assertNull(ItemMapper.toDto(item));
    }

    @Test
    void fromDtoTestIfItemDtoNull() {
        assertNull(ItemMapper.fromDto(itemDto));
    }

    @Test
    void toDtoResponseFieldNull() {
        assertNull(ItemMapper.toDtoResponse(item, nextBooking, lastBooking, comments));
    }

    @Test
    void itemRequestIdItemNull() {
        Request request = new Request(null, "text", new User(), LocalDateTime.now());
        assertNull(ItemMapper.itemRequestId(item));
    }
}