package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemDto.ItemDtoBuilder itemDto = ItemDto.builder();
        itemDto.requestId(itemRequestId(item));
        if (item.getId() != null) {
            itemDto.id(item.getId());
        }
        itemDto.name(item.getName());
        itemDto.description(item.getDescription());
        itemDto.available(item.getAvailable());
        return itemDto.build();
    }

    public static Item fromDto(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }
        Item.ItemBuilder item = Item.builder();
        item.id(itemDto.getId());
        item.name(itemDto.getName());
        item.description(itemDto.getDescription());
        item.available(itemDto.getAvailable());
        item.owner(itemDto.getOwner());
        return item.build();
    }

    public static ItemDtoResponse toDtoResponse(Item item, BookingDtoShort nextBooking, BookingDtoShort lastBooking, List<CommentDto> comments) {
        if (item == null && nextBooking == null && lastBooking == null && comments == null) {
            return null;
        }
        ItemDtoResponse.ItemDtoResponseBuilder itemDtoResponse = ItemDtoResponse.builder();
        if (item != null) {
            itemDtoResponse.id(item.getId());
            itemDtoResponse.name(item.getName());
            itemDtoResponse.description(item.getDescription());
            itemDtoResponse.available(item.getAvailable());
            itemDtoResponse.owner(item.getOwner());
        }
        itemDtoResponse.nextBooking(nextBooking);
        itemDtoResponse.lastBooking(lastBooking);
        if (comments != null) {
            itemDtoResponse.comments(new ArrayList<CommentDto>(comments));
        }
        return itemDtoResponse.build();
    }

    private static Long itemRequestId(Item item) {
        if (item == null) {
            return null;
        }
        Request request = item.getRequest();
        if (request == null) {
            return null;
        }
        Long id = request.getId();
        if (id == null) {
            return null;
        }
        return id;
    }
}