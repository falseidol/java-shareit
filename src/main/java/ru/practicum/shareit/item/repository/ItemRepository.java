package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemRepository {

    Map<Long, Item> getItemMap();

    Item create(Long userId, Item item);
    Item getItemById(Long itemId);

    List<Item> findAll(Long userId);

    List<Item> searchItem(String text);
}
