package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.HeaderNotFoundException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        log.info("Добавление вещи");
        if (userId == null) {
            throw new HeaderNotFoundException("Заголовок не найден");
        }
        if (!userRepository.getUserMap().containsKey(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Item item = ItemMapper.fromItemDtoToItem(itemDto);
        itemRepository.create(userId, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId){
        Item itemUpdate = itemRepository.getItemById(itemId);
        log.info("Обновление вещи");
        if (userId == null) {
            throw new ObjectNotFoundException("Не передан заголовок с ид пользователя");
        }
        if (!userRepository.getUserMap().containsKey(userId)) {
            throw new UserNotFoundException("Такого пользователя нету");
        }
        if (itemUpdate.getOwner() != null && itemUpdate.getOwner().getId() != userId) {
            throw new UserNotFoundException("У пользователя нет вещей");
        }
        if (itemDto.getName() != null) {
            itemUpdate.setName(itemDto.getName());
        }
        if (itemDto.getAvailable() != null) {
            itemUpdate.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            itemUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getRequest() != null) {
            itemUpdate.setRequest(itemDto.getRequest());
        }
        return ItemMapper.toItemDto(itemUpdate);
    }

    @Override
    public Collection<ItemDto> findAll(Long userId) {
        Collection<Item> items = itemRepository.findAll(userId);
        log.info("Возвращаем список вещей");
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        if (!userRepository.getUserMap().containsKey(userId)) {
            throw new UserNotFoundException("Такого пользователя нету");
        }
        log.info("Возвращаем вещи по ид");
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<Item> itemList = itemRepository.searchItem(text);
        return itemList.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id) {
        if (!itemRepository.getItemMap().containsKey(id)) {
            throw new UserNotFoundException("Вещь не найдена");
        }
        log.info("Вещь по ид");
        return ItemMapper.toItemDto(itemRepository.getItemById(id));
    }
}
