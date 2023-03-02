package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void toDtoTestIfItemIdNull() {
        User user = new User(1L, "bebr", "abobovich");
        Request request = new Request(null, "text", user, LocalDateTime.now());
        Item item = new Item(1L, "drill", "text", true, user, request);
        ItemDto itemDto1 = ItemMapper.toDto(item);
        assertEquals(itemDto1.getId(), item.getId());
        assertEquals(itemDto1.getName(), item.getName());
        assertEquals(itemDto1.getDescription(), item.getDescription());
        assertEquals(itemDto1.getAvailable(), item.getAvailable());
    }

    @Test
    void fromDtoTestIfItemDtoNull() {
        assertNull(ItemMapper.fromDto(itemDto));
    }

    @Test
    void toDtoResponseFieldsNull() {
        assertNull(ItemMapper.toDtoResponse(item, nextBooking, lastBooking, comments));
    }

    @Test
    void toDtoResponseItemNull() {
        User user = new User(1L, "bebr", "abobovich");
        nextBooking = new BookingDtoShort(1L, user.getId());
        lastBooking = new BookingDtoShort(1L, user.getId());
        Request request = new Request(1L, "text", user, LocalDateTime.now());
        Item item = new Item(1L, "drill", "text", true, user, request);
        ItemDtoResponse itemDtoResponse = ItemMapper.toDtoResponse(item, nextBooking, lastBooking, comments);
        assertEquals(itemDtoResponse.getId(), (item.getId()));
        assertEquals(itemDtoResponse.getName(), (item.getName()));
        assertEquals(itemDtoResponse.getDescription(), (item.getDescription()));
        assertEquals(itemDtoResponse.getAvailable(), (item.getAvailable()));
    }

    @Test
    void toDtoResponseItemNull2() {
        User user = new User(1L, "bebr", "abobovich");
        nextBooking = new BookingDtoShort(1L, user.getId());
        lastBooking = new BookingDtoShort(1L, user.getId());
        Item item = null;
        ItemDtoResponse itemDtoResponse = ItemMapper.toDtoResponse(item, nextBooking, lastBooking, comments);
        assertNull(itemDtoResponse.getId());
        assertNull(itemDtoResponse.getName());
        assertNull(itemDtoResponse.getDescription());
        assertNull(itemDtoResponse.getAvailable());
    }

    @Test
    void itemRequestIdItemNull() {
        User user = new User(1L, "bebr", "abobovich");
        Request request = new Request(null, "text", user, LocalDateTime.now());
        Item item = new Item(1L, "drill", "text", true, user, request);
        assertNull(ItemMapper.itemRequestId(item));
    }

    @Test
    void itemRequestIdRequestIdNull() {
        assertNull(ItemMapper.itemRequestId(item));
    }
}