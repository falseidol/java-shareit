package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long id);

    Collection<ItemDto> findAll(Long userId);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> searchItem(String text);

    ItemDto getById(Long id);
}
