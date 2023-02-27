package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long id);

    Collection<ItemDtoResponse> findAll(Long userId,Integer from,Integer size);

    ItemDtoResponse getItemById(Long userId, Long itemId);

    List<ItemDto> searchItem(Integer from,Integer size,String text);

    CommentDto postComment(Long userId, Long itemId, CommentDto commentDto);
}