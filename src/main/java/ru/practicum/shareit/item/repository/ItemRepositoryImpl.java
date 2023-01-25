package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final UserRepository userRepository;
    private final HashMap<Long, List<Item>> itemsByUserId = new HashMap<>();
    private final Map<Long, Item> itemMap = new HashMap<>();
    private Long itemId = 1L;

    @Override
    public Map<Long, Item> getItemMap() {
        return itemMap;
    }

    @Override
    public Item create(Long userId, Item item) {
        List<Item> itemList = new ArrayList<>();
        if (itemsByUserId.containsKey(userId))
            itemList = itemsByUserId.get(userId);
        item.setId(itemId++);
        item.setOwner(userRepository.getUserFromMap(userId));
        itemList.add(item);

        itemMap.put(item.getId(), item);

        itemsByUserId.put(userId, itemList);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemMap.get(itemId);
    }

    @Override
    public List<Item> findAll(Long userId) {
        return itemsByUserId.get(userId);
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> itemList = new ArrayList<>();
        String search = text.toLowerCase();
        if (!text.isBlank()) {
            itemList = itemMap.values().stream()
                    .filter(item -> item.getAvailable() == true)
                    .filter(item -> item.getName().toLowerCase().contains(search)
                            || item.getDescription().toLowerCase().contains(search))
                    .collect(Collectors.toList());
        }
        return itemList;
    }
}
